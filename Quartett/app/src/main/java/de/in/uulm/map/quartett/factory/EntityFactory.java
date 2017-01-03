package de.in.uulm.map.quartett.factory;

import android.support.annotation.Nullable;
import android.telecom.Call;

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

import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private Deck mDeck;

    /**
     * This will be set to the currently loaded DeckInfo.
     */
    private DeckInfo mDeckInfo;

    /**
     * All mCards that are loaded by the factory are registered here. This is
     * needed as the objects must be known later for saving.
     */
    private ArrayList<Card> mCards;

    /**
     * All Attribute objects that have been loaded.
     */
    private ArrayList<Attribute> mAttributes;

    /**
     * All AttributeValue objects that have been loaded.
     */
    private ArrayList<AttributeValue> mAttributeValues;

    /**
     * All Image objects that have been loaded.
     */
    private ArrayList<Image> mImages;

    /**
     * All Card objects that have been loaded.
     */
    private ArrayList<CardImage> mCardImages;

    /**
     * A Json Loader the Deck definition can be loaded from.
     */
    private JsonLoader mJsonLoader;

    /**
     * Simple constructor to initialize the member Variables.
     *
     * @param mJsonLoader a JsonLoader to load the Deck definition from
     */
    public EntityFactory(JsonLoader mJsonLoader) {

        mDeck = null;
        mDeckInfo = null;
        mAttributes = new ArrayList<>();
        mAttributeValues = new ArrayList<>();
        mImages = new ArrayList<>();
        mCardImages = new ArrayList<>();
        mCards = new ArrayList<>();

        this.mJsonLoader = mJsonLoader;
    }

    /**
     * This method will construct a Deck object from a JSON file. No image paths
     * in the JSON file will be touched. This may result in incorrect paths when
     * using relative paths in the JSON file or when loading from an online
     * source.
     *
     * @return a fully constructed and filled Deck
     */
    public Deck loadDeck() throws JSONException, IOException {

        if (mDeck != null) {
            return mDeck;
        }

        JSONObject jsonDeck = mJsonLoader.getJson();

        JSONArray jsonCards = jsonDeck.getJSONArray("cards");
        JSONArray jsonAttributes = jsonDeck.getJSONArray("properties");

        mDeck = new Deck(
                jsonDeck.getString("name"),
                jsonDeck.getString("description"),
                null);

        mDeckInfo = new DeckInfo(
                mDeck,
                mJsonLoader.getSource(),
                mJsonLoader.getHash(),
                new Date().getTime());

        HashMap<Integer, Attribute> attrs = new HashMap<>();

        for (int i = 0; i < jsonAttributes.length(); i++) {
            JSONObject jsonAttr = (JSONObject) jsonAttributes.get(i);
            Attribute attr = loadAttribute(jsonAttr, mDeck);
            int id = jsonAttr.getInt("id");
            attrs.put(id, attr);
        }

        for (int i = 0; i < jsonCards.length(); i++) {
            loadCard((JSONObject) jsonCards.get(i), attrs, mDeck);
        }

        return mDeck;
    }

    /**
     * This will expose all loaded images of this EntityFactory. That is, all
     * images of the currently loaded Deck or no images if no Deck has been
     * loaded. This is needed to alter the image paths as they may point to
     * invalid locations.
     *
     * @return a List of all currently loaded Imgages
     */
    public List<Image> getImages() {

        return mImages;
    }

    /**
     * This will save the all the Entities that factory has loaded earlier in
     * the correct order. It is discouraged to save entities any other way. The
     * methods is async and returns immediately.
     *
     * @param callback be notified when the transaction is completed
     */
    protected void save(@Nullable final Callback callback) {

        SugarTransactionHelper.doInTransaction(
                new SugarTransactionHelper.Callback() {

                    @Override
                    public void manipulateInTransaction() {

                        if (mDeck == null) {
                            return;
                        }

                        mDeck.save();
                        mDeckInfo.save();
                        SugarRecord.saveInTx(mAttributes);
                        SugarRecord.saveInTx(mCards);
                        SugarRecord.saveInTx(mAttributeValues);
                        SugarRecord.saveInTx(mImages);
                        SugarRecord.saveInTx(mCardImages);

                        if (callback != null) {
                            callback.onSaved();
                        }
                    }
                });
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
        mCards.add(card);

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

        mAttributes.add(attr);

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

        mAttributeValues.add(attributeValue);

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

        mImages.add(image);

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

        mCardImages.add(cardImage);

        return cardImage;
    }

    /**
     * Use this interface to be notified when the deck has be fully saved to the
     * database.
     */
    public interface Callback {

        void onSaved();
    }
}
