package de.in.uulm.map.quartett.factory;

import com.google.common.collect.Lists;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;

import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jona on 12/22/16.
 */

/**
 * This class can be used to construct a Deck object from a given JSON file.
 */
public class EntityFactory {
/*
    *//**
     * This is the context the Factory lives in.
     * It is usually given by the activity.
     *//*
    private Context context;

    *//**
     * Simple constructor to hand over the current Context.
     *
     * @param context the current Context
     *//*
    public EntityFactory(Context context) {

        this.context = context;
    }

    *//**
     * Use this method to construct a Deck object from a JSON file.
     * No image paths in the JSON file will be touched.
     * This may result in incorrect paths when using relative paths in the
     * JSON file or when loading from an online source.
     *
     * @param jsonDeck the JSONObject to construct the Deck from
     * @return a fully constructed and filled Deck
     * @throws JSONException
     *//*
    private Deck getDeck(JSONObject jsonDeck) throws JSONException {

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

        return new Deck(
                jsonDeck.getString("name"),
                jsonDeck.getString("description"),
                null,
                cards,
                Lists.newArrayList(attrs.values()));
    }

    *//**
     * This method will construct a Deck from a folder in the assets directory.
     * Also the image paths will be altered to point to the correct image
     * locations.
     *
     * @param path the path of the JSON file e.g. "bikes/bikes.json"
     * @return a fully constructed and filled Deck
     * @throws JSONException
     *//*
    public Deck getDeckFromAssets(String path) throws JSONException, IOException {

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

        Deck deck = getDeck(new JSONObject(stringWriter.toString()));

        // alter all the image paths to point to the right location
        // in the assets directory

        String dir = new File("file:///android_asset/" + path).getParent();

        for(Card c : deck.mCards) {
            for(Image i : c.mImages) {
                i.mUri = Uri.parse(dir + "/" + i.mUri.getPath());
            }
        }

        return deck;
    }

    *//**
     * Use this method to construct a Card object from a JSONObject.
     * The method will store all images linked in the JSON file in internal
     * storage. This call may take some time.
     *
     * @param jsonCard the JSONObject to construct the Card from
     * @param attrs the Attribute of the Deck the card is associated with
     * @return a fully constructed Card object
     * @throws JSONException
     *//*
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

        return new Card(
                jsonCard.getString("name"),
                images,
                attributeValues);
    }

    *//**
     * Use this method to construct an Attribute Object from a JSONObject.
     *
     * @param jsonAttribute the JSONObject to construct the Attribute from
     * @return a fully constructed Attribute object
     * @throws JSONException
     *//*
    private Attribute getAttribute(JSONObject jsonAttribute)
            throws JSONException {

        return new Attribute(
                jsonAttribute.getString("text"),
                jsonAttribute.getString("unit"),
                (jsonAttribute.getInt("compare") == 1));
    }

    *//**
     * Use this method to construct an AttributeValue from a JSONObject.
     *
     * @param jsonAttributeValue the JSONObject to construct the
     *                           AttributeValue from
     * @param attrs the Attribute of the Card the AttributeValue belongs to
     * @return a fully constructed AttributeValue object
     * @throws JSONException
     *//*
    private AttributeValue getAttributeValue(
            JSONObject jsonAttributeValue,
            HashMap<Integer, Attribute> attrs) throws JSONException {

        return new AttributeValue(
                (float) jsonAttributeValue.getDouble("value"),
                attrs.get(jsonAttributeValue.getInt("propertyId")));
    }

    *//**
     * Use this method to create an Image from a JSONObject.
     * The paths of the object may point to locations not known by the system.
     * Therefore further processing is needed to alter the paths so that they
     * point to a valid location.
     *
     * @param jsonImage the JSONObject to construct the Image from
     * @return an Image object with the image path from the JSON object
     * @throws JSONException
     *//*
    private Image getImage(JSONObject jsonImage) throws JSONException {

        return new Image(
                Uri.parse(jsonImage.getString("filename")),
                jsonImage.optString("description"));
   }*/
}
