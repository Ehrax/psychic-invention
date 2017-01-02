package de.in.uulm.map.quartett.factory;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.CardImage;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * Created by jona on 12/22/16.
 */

/**
 * This class can be used to construct a Deck object from a given JSON file.
 */
public class EntityFactory {

    /**
     * This is the context the Factory lives in. It is usually given by the
     * activity.
     */
    private Context context;

    /**
     * Simple constructor to hand over the current Context.
     *
     * @param context the current Context
     */
    public EntityFactory(Context context) {

        this.context = context;
    }

    /**
     * Use this method to construct a Deck object from a JSON file. No image
     * paths in the JSON file will be touched. This may result in incorrect
     * paths when using relative paths in the JSON file or when loading from an
     * online source.
     *
     * @param jsonDeck the JSONObject to construct the Deck from
     * @return a fully constructed and filled Deck
     */
    private Deck importDeck(JSONObject jsonDeck) throws JSONException {

        // maybe add the names of the elements to strings.xml

        JSONArray jsonCards = jsonDeck.getJSONArray("cards");
        JSONArray jsonAttributes = jsonDeck.getJSONArray("properties");

        Deck deck = new Deck(
                jsonDeck.getString("name"),
                jsonDeck.getString("description"),
                null);

        deck.save();

        HashMap<Integer, Attribute> attrs = new HashMap<>();

        for (int i = 0; i < jsonAttributes.length(); i++) {
            JSONObject jsonAttr = (JSONObject) jsonAttributes.get(i);
            Attribute attr = importAttribute(jsonAttr, deck);
            int id = jsonAttr.getInt("id");
            attrs.put(id, attr);
        }

        for (int i = 0; i < jsonCards.length(); i++) {
            importCard((JSONObject) jsonCards.get(i), attrs, deck);
        }

        return deck;
    }

    /**
     * This method will construct a Deck from a folder in the assets directory.
     * Also the image paths will be altered to point to the correct image
     * locations.
     *
     * @param path the path of the JSON file e.g. "bikes/bikes.json"
     * @return a fully constructed and filled Deck
     */
    public Deck importDeckFromAssets(String path)
            throws JSONException, IOException {

        // read in the JSON file
        // no sure if this is still a task to be done by a factory ...

        AssetManager am = context.getAssets();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(am.open(path)));

        StringWriter stringWriter = new StringWriter();

        char[] buffer = new char[1024];

        int n;
        while ((n = reader.read(buffer)) != -1) {
            stringWriter.write(buffer, 0, n);
        }

        Deck deck = importDeck(new JSONObject(stringWriter.toString()));

        // alter all the image paths to point to the right location
        // in the assets directory

        String dir = new File("file:///android_asset/" + path).getParent();

        for (Card c : deck.getCards()) {
            for (CardImage ci : c.getCardImages()) {
                ci.mImage.mUri =
                        Uri.parse(dir + "/" + ci.mImage.mUri.getPath());
                ci.mImage.save();
            }
        }

        return deck;
    }

    /**
     * Use this method to import a Card from a JSONObject into the database.
     *
     * @param jsonCard the JSONObject to construct the Card from
     * @param attrs    the Attribute of the Deck the card is associated with
     * @return a fully constructed Card object
     */
    private Card importCard(JSONObject jsonCard,
                            HashMap<Integer, Attribute> attrs,
                            Deck deck) throws JSONException {

        JSONArray jsonImages = jsonCard.getJSONArray("images");
        JSONArray jsonAttributeValues = jsonCard.getJSONArray("values");

        Card card = new Card(jsonCard.getString("name"), deck);
        card.save();

        for (int i = 0; i < jsonImages.length(); i++) {
            importCardImage((JSONObject) jsonImages.get(i), card);
        }

        for (int i = 0; i < jsonAttributeValues.length(); i++) {
            importAttributeValue((JSONObject) jsonAttributeValues.get(i),
                    attrs, card);
        }

        return card;
    }

    /**
     * Use this method to construct an Attribute Object from a JSONObject. The
     * created object will also be save to the database.
     *
     * @param jsonAttribute the JSONObject to construct the Attribute from
     * @return a fully constructed Attribute object
     */
    private Attribute importAttribute(JSONObject jsonAttribute, Deck deck)
            throws JSONException {

        Attribute attr = new Attribute(
                jsonAttribute.getString("text"),
                jsonAttribute.getString("unit"),
                (jsonAttribute.getInt("compare") == 1),
                deck);

        attr.save();

        return attr;
    }

    /**
     * Use this method to construct an AttributeValue from a JSONObject. The
     * created object will also be saved to the database.
     *
     * @param jsonAttributeValue the JSONObject to construct the AttributeValue
     *                           from
     * @param attrs              the Attribute of the Card the AttributeValue
     *                           belongs to
     * @param card               the Card object the AttributeValue is
     *                           associated with
     * @return a fully constructed AttributeValue object
     */
    private AttributeValue importAttributeValue(JSONObject jsonAttributeValue,
                                                HashMap<Integer, Attribute> attrs,
                                                Card card) throws JSONException {

        AttributeValue attributeValue = new AttributeValue(
                (float) jsonAttributeValue.getDouble("value"),
                attrs.get(jsonAttributeValue.getInt("propertyId")),
                card);

        attributeValue.save();

        return attributeValue;
    }

    /**
     * Use this method to import an Image from a JSONObject into the database.
     * The paths of the object may point to locations not known by the system.
     * Therefore further processing is needed to alter the paths so that they
     * point to a valid location.
     *
     * @param jsonImage the JSONObject to construct the Image from
     * @return an Image object with the image path from the JSON object
     */
    private Image importImage(JSONObject jsonImage)
            throws JSONException {

        Image image = new Image(
                Uri.parse(jsonImage.getString("filename")),
                jsonImage.optString("description"));

        image.save();

        return image;
    }

    /**
     * Use this method to import a CardImage from a JSONObject into the
     * database. The paths of the image member variable may point to locations
     * not known by the system. Therefore further processing is needed to alter
     * the paths so that they point to a valid location.
     *
     * @param jsonCardImage the JSONObject to construct the CardImage from
     * @param card          the Card object the CardImage will be associated
     *                      with
     * @return an Image object with the image path from the JSON object
     */
    private CardImage importCardImage(JSONObject jsonCardImage, Card card)
            throws JSONException {

        CardImage cardImage = new CardImage(card, importImage(jsonCardImage));

        cardImage.save();

        return cardImage;
    }
}
