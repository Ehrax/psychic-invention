package de.in.uulm.map.quartett.factory;

import android.content.Context;
import android.content.res.AssetManager;

import com.orm.SugarRecord;
import com.orm.SugarTransactionHelper;

import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.CardImage;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.DeckInfo;
import de.in.uulm.map.quartett.data.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jona on 12/22/16.
 */

/**
 * This class can be used to construct a Deck object from a given JSON file.
 */
public class EntityFactory {

    /**
     * This will be set to the currently loaded Deck.
     */
    private Deck deck;

    /**
     * This will be set to the currently loaded DeckInfo.
     */
    private DeckInfo deckInfo;

    /**
     * All cards that are loaded by the factory are registered here. This is
     * needed as the objects must be known later for saving.
     */
    private ArrayList<Card> cards;

    /**
     * All Attribute objects that have been loaded.
     */
    private ArrayList<Attribute> attributes;

    /**
     * All AttributeValue objects that have been loaded.
     */
    private ArrayList<AttributeValue> attributeValues;

    /**
     * All Image objects that have been loaded.
     */
    private ArrayList<Image> images;

    /**
     * All Card objects that have been loaded.
     */
    private ArrayList<CardImage> cardImages;

    /**
     * The constructor will construct a Deck object from a JSON file. No image
     * paths in the JSON file will be touched. This may result in incorrect
     * paths when using relative paths in the JSON file or when loading from an
     * online source.
     *
     * @param jsonDeck the JSONObject to construct the Deck from
     * @param source   the source the Deck was loaded from e.g.
     *                 "http://asdf..."
     * @return a fully constructed and filled Deck
     */
    public EntityFactory(JSONObject jsonDeck, String source)
            throws JSONException {

        deck = null;
        deckInfo = null;
        attributes = new ArrayList<>();
        attributeValues = new ArrayList<>();
        images = new ArrayList<>();
        cardImages = new ArrayList<>();
        cards = new ArrayList<>();

        // maybe add the names of the elements to strings.xml

        JSONArray jsonCards = jsonDeck.getJSONArray("cards");
        JSONArray jsonAttributes = jsonDeck.getJSONArray("properties");

        deck = new Deck(
                jsonDeck.getString("name"),
                jsonDeck.getString("description"),
                null);

        deckInfo = new DeckInfo(
                deck,
                source,
                jsonDeck.toString().hashCode(),
                new Date(new java.util.Date().getTime()));

        HashMap<Integer, Attribute> attrs = new HashMap<>();

        for (int i = 0; i < jsonAttributes.length(); i++) {
            JSONObject jsonAttr = (JSONObject) jsonAttributes.get(i);
            Attribute attr = loadAttribute(jsonAttr, deck);
            int id = jsonAttr.getInt("id");
            attrs.put(id, attr);
        }

        for (int i = 0; i < jsonCards.length(); i++) {
            loadCard((JSONObject) jsonCards.get(i), attrs, deck);
        }
    }

    /**
     * This will save the all the Entities that factory has loaded earlier in
     * the correct order. It is discouraged to save entities any other way.
     */
    protected void save() {

        SugarTransactionHelper.doInTransaction(
                new SugarTransactionHelper.Callback() {

                    @Override
                    public void manipulateInTransaction() {

                        if (deck == null) {
                            return;
                        }

                        deck.save();
                        deckInfo.save();
                        SugarRecord.saveInTx(attributes);
                        SugarRecord.saveInTx(cards);
                        SugarRecord.saveInTx(attributeValues);
                        SugarRecord.saveInTx(images);
                        SugarRecord.saveInTx(cardImages);
                    }
                });
    }

    /**
     * This method will construct a Deck from a folder in the assets directory.
     * Also the image paths will be altered to point to the correct image
     * locations.
     *
     * @param path the path of the JSON file e.g. "decks/bikes/bikes.json"
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

        Deck deck = loadDeck(new JSONObject(stringWriter.toString()));

        // alter all the image paths to point to the right location
        // in the assets directory

        String dir = new File("file:///android_asset/" + path).getParent();

        for (Card c : deck.getCards()) {
            for (CardImage ci : c.getCardImages()) {
                ci.mImage.mUri = dir + "/" + ci.mImage.mUri;
                ci.mImage.save();
            }
        }

        return deck;
    }

    /**
     * Use this method to construct a Card from a JSONObject.
     *
     * @param jsonCard the JSONObject to construct the Card from
     * @param attrs    the Attribute of the Deck the card is associated with
     * @return a fully constructed Card object
     */
    private Card loadCard(JSONObject jsonCard,
                          HashMap<Integer, Attribute> attrs,
                          Deck deck) throws JSONException {

        JSONArray jsonImages = jsonCard.getJSONArray("images");
        JSONArray jsonAttributeValues = jsonCard.getJSONArray("values");

        Card card = new Card(jsonCard.getString("name"), deck);
        cards.add(card);

        for (int i = 0; i < jsonImages.length(); i++) {
            loadCardImage((JSONObject) jsonImages.get(i), card);
        }

        for (int i = 0; i < jsonAttributeValues.length(); i++) {
            loadAttributeValue((JSONObject) jsonAttributeValues.get(i),
                    attrs, card);
        }

        return card;
    }

    /**
     * Use this method to construct an Attribute Object from a JSONObject.
     *
     * @param jsonAttribute the JSONObject to construct the Attribute from
     * @return a fully constructed Attribute object
     */
    private Attribute loadAttribute(JSONObject jsonAttribute, Deck deck)
            throws JSONException {

        Attribute attr = new Attribute(
                jsonAttribute.getString("text"),
                jsonAttribute.getString("unit"),
                (jsonAttribute.getInt("compare") == 1),
                deck);

        attributes.add(attr);

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
    private AttributeValue loadAttributeValue(JSONObject jsonAttributeValue,
                                              HashMap<Integer, Attribute> attrs,
                                              Card card) throws JSONException {

        AttributeValue attributeValue = new AttributeValue(
                (float) jsonAttributeValue.getDouble("value"),
                attrs.get(jsonAttributeValue.getInt("propertyId")),
                card);

        attributeValues.add(attributeValue);

        return attributeValue;
    }

    /**
     * Use this method to construct an Image from a JSONObject. The paths of the
     * object may point to locations not known by the system. Therefore further
     * processing is needed to alter the paths so that they point to a valid
     * location.
     *
     * @param jsonImage the JSONObject to construct the Image from
     * @return an Image object with the image path from the JSON object
     */
    private Image loadImage(JSONObject jsonImage)
            throws JSONException {

        Image image = new Image(
                jsonImage.getString("filename"),
                jsonImage.optString("description"));

        images.add(image);

        return image;
    }

    /**
     * Use this method to construct a CardImage from a JSONObject. The paths of
     * the image member variable may point to locations not known by the system.
     * Therefore further processing is needed to alter the paths so that they
     * point to a valid location.
     *
     * @param jsonCardImage the JSONObject to construct the CardImage from
     * @param card          the Card object the CardImage will be associated
     *                      with
     * @return an Image object with the image path from the JSON object
     */
    private CardImage loadCardImage(JSONObject jsonCardImage, Card card)
            throws JSONException {

        CardImage cardImage = new CardImage(card, loadImage(jsonCardImage));

        cardImages.add(cardImage);

        return cardImage;
    }
}
