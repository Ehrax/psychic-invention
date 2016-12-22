package de.in.uulm.map.quartett.factory;

import com.google.common.collect.Lists;

import android.content.Context;
import android.net.Uri;

import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
     * This is the context the Factory lives in.
     * It is usually given by the activity.
     */
    Context context;

    /**
     * Simple constructor to hand over the current Context.
     *
     * @param context the current Context
     */
    public EntityFactory(Context context) {

        this.context = context;
    }

    /**
     * Use this method to construct a Deck object from a JSON file.
     * All images linked in the JSON file will be stored in internal storage.
     * This method can take some time so call it async.
     *
     * @param jsonDeck the JSONObject to construct the Deck from
     * @return a fully constructed and filled Deck
     * @throws JSONException
     */
    public Deck getDeck(JSONObject jsonDeck) throws JSONException {

        // maybe add the names of the elements to strings.xml

        JSONArray jsonCards = jsonDeck.getJSONArray("cards");
        JSONArray jsonAttributes = jsonDeck.getJSONArray("properties");

        HashMap<Integer, Attribute> attrs = new HashMap<>();

        for (int i = 0; i < jsonAttributes.length(); i++) {
            JSONObject jsonAttr = (JSONObject) jsonAttributes.get(i);
            Attribute attr = getAttribute(jsonAttr);
            int id = jsonAttr.getInt("id");
            attrs.put(id, attr);
        }

        ArrayList<Card> cards = new ArrayList<>();

        for (int i = 0; i < jsonCards.length(); i++) {
            cards.add(getCard((JSONObject) jsonCards.get(i), attrs));
        }

        Deck deck = new Deck(
                jsonDeck.getString("name"),
                jsonDeck.getString("description"),
                null,
                cards,
                Lists.newArrayList(attrs.values()));

        return deck;
    }

    /**
     * Use this method to construct a Card object from a JSONObject.
     * The method will store all images linked in the JSON file in internal
     * storage. This call may take some time.
     *
     * @param jsonCard the JSONObject to construct the Card from
     * @param attrs the Attribute of the Deck the card is associated with
     * @return a fully constructed Card object
     * @throws JSONException
     */
    private Card getCard(
            JSONObject jsonCard,
            HashMap<Integer, Attribute> attrs) throws JSONException {

        JSONArray jsonImages = jsonCard.getJSONArray("images");
        JSONArray jsonAttributeValues = jsonCard.getJSONArray("values");

        ArrayList<Image> images = new ArrayList<>();

        for (int i = 0; i < jsonImages.length(); i++) {
            images.add(getImage((JSONObject) jsonImages.get(i)));
        }

        ArrayList<AttributeValue> attributeValues = new ArrayList<>();

        for (int i = 0; i < jsonAttributeValues.length(); i++) {
            attributeValues.add(getAttributeValue(
                    (JSONObject) jsonAttributeValues.get(i),
                    attrs));
        }

        Card card = new Card(
                jsonCard.getString("name"),
                images,
                attributeValues);

        return card;
    }

    /**
     * Use this method to construct an Attribute Object from a JSONObject.
     *
     * @param jsonAttribute the JSONObject to construct the Attribute from
     * @return a fully constructed Attribute object
     * @throws JSONException
     */
    private Attribute getAttribute(JSONObject jsonAttribute)
            throws JSONException {

        Attribute attribute = new Attribute(
                jsonAttribute.getString("text"),
                jsonAttribute.getString("unit"),
                (jsonAttribute.getInt("compare") == 1));

        return attribute;
    }

    /**
     * Use this method to construct an AttributeValue from a JSONObject.
     *
     * @param jsonAttributeValue the JSONObject to construct the
     *                           AttributeValue from
     * @param attrs the Attribute of the Card the AttributeValue belongs to
     * @return a fully constructed AttributeValue object
     * @throws JSONException
     */
    private AttributeValue getAttributeValue(
            JSONObject jsonAttributeValue,
            HashMap<Integer, Attribute> attrs) throws JSONException {

        AttributeValue attributeValue = new AttributeValue(
                (float) jsonAttributeValue.getDouble("value"),
                attrs.get(jsonAttributeValue.getInt("propertyId")));

        return attributeValue;
    }

    /**
     * Use this method to create an Image from a JSONObject and store the
     * associated image in internal storage for later use.
     * This may take some time, as the image may be loaded from online sources.
     *
     * @param jsonImage the JSONObject to construct the Image from
     * @return a Image object linked with the locally stored image
     * @throws JSONException
     */
    private Image getImage(JSONObject jsonImage) throws JSONException {

        String path = jsonImage.getString("filename");

        // load the image into local storage here
        // and use the local uri instead of path

        Image image = new Image(
                Uri.parse(path),
                jsonImage.optString("description"));

        return image;
    }
}
