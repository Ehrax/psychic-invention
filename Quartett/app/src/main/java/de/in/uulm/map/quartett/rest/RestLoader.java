package de.in.uulm.map.quartett.rest;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

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
import org.w3c.dom.Attr;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Created by Jona on 21.01.2017.
 */

public class RestLoader {

    private static final String SERVER_URL = "http://quartett.af-mba.dbis.info";
    private static final String SERVER_AUTH = "Basic YWRtaW46ZGIxJGFkbWlu";

    private final Context mContext;
    private final RequestQueue mRequestQueue;
    private final ImageLoader mImageLoader;

    /**
     * This function is used to construct the correct HTTP header containing the
     * necessary authorization information and content type.
     *
     * @return a HashMap containing the HTTP header key value pairs
     */
    public static HashMap<String, String> getAuthHeader() {

        HashMap<String, String> map = new HashMap<>();
        map.put("Authorization", SERVER_AUTH);

        return map;
    }

    /**
     * The constructor creates a request queue and initializes member
     * variables.
     *
     * @param context the current context, needed to create message queue
     */
    public RestLoader(Context context) {

        mContext = context;
        mRequestQueue = Volley.newRequestQueue(mContext);
        mImageLoader = new ImageLoader(
                mRequestQueue,
                new LruBitmapCache(LruBitmapCache.getCacheSize(context)));
    }

    /**
     * This will cancel all running Volley requests. Should be called when the
     * activity is destroyed.
     */
    public void cancelAll() {

        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {

                return true;
            }
        });
    }

    /**
     * This method should be used to load all decks from the server. Note that
     * this will retrieve only the information necessary to construct the Deck
     * objects but will not download the contained Cards.
     *
     * @param deckListener  will be called when the Decks are ready
     * @param errorListener will be called on any errors
     */
    public void loadAllDecks(final Response.Listener<List<Deck>> deckListener,
                             final Response.ErrorListener errorListener) {

        AuthJsonArrayRequest jsonArrayRequest = new AuthJsonArrayRequest(
                Request.Method.GET,
                SERVER_URL + "/decks",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ArrayList<Deck> decks = new ArrayList<>();

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                final JSONObject obj = response.getJSONObject(i);

                                final Image image = new Image(
                                        obj.getString("image"),
                                        obj.getString("name"));

                                final DeckInfo deckInfo = new DeckInfo(
                                        SERVER_URL + "/decks/" + obj.getInt("id"),
                                        obj.toString().hashCode(),
                                        new Date().getTime(),
                                        false);

                                final Deck deck = new Deck(
                                        obj.getString("name"),
                                        obj.getString("description"),
                                        image,
                                        deckInfo);

                                decks.add(deck);
                            } catch (JSONException e) {
                                errorListener.onErrorResponse(new VolleyError(e));
                            }
                        }

                        deckListener.onResponse(decks);
                    }
                },
                errorListener);

        mRequestQueue.add(jsonArrayRequest);
    }

    /**
     * Given a url this method will asynchronously load an Image into an
     * ImageView.
     *
     * @param url       the url of the Image
     * @param imageView the image view to load the Image into
     */
    public void loadImage(String url,
                          ImageView imageView,
                          int placeholderDrawable,
                          int errorDrawable) {

        mImageLoader.get(url, ImageLoader.getImageListener(imageView,
                placeholderDrawable, errorDrawable));
    }

    /**
     * An instance of this special class is passed to the deck loading methods
     * to collect all the data in a single object.
     */
    public class Collector {

        public Deck mDeck;
        public DeckInfo mDeckInfo;

        public ArrayList<Image> mImages = new ArrayList<>();
        public ArrayList<CardImage> mCardImages = new ArrayList<>();
        public ArrayList<Card> mCards = new ArrayList<>();
        public ArrayList<Attribute> mAttributes = new ArrayList<>();
        public ArrayList<AttributeValue> mAttributeValues = new ArrayList<>();

        public Collector() {

        }
    }

    /**
     * This method should be used download and store a Deck in the internal
     * Storage.
     *
     * @param id the id of the Deck to be downloaded
     */
    public Collector loadDeck(int id)
            throws InterruptedException, ExecutionException, JSONException {

        Collector c = new Collector();
        loadDeck(id, c);

        return c;
    }

    /**
     * This method will load the deck and all containing cards and will store
     * everything into the Collector. Synchronous call, will block!
     *
     * @param id the id of the Deck to load
     * @param c  the collector object
     */
    private void loadDeck(int id, Collector c)
            throws ExecutionException, InterruptedException, JSONException {

        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        AuthJsonObjectRequest req = new AuthJsonObjectRequest(
                Request.Method.GET,
                SERVER_URL + "/decks/" + id,
                null,
                future,
                future);

        mRequestQueue.add(req);

        JSONObject res = future.get();

        final Image image = new Image(
                res.getString("image"),
                res.getString("name"));

        c.mImages.add(image);

        c.mDeckInfo = new DeckInfo(
                SERVER_URL + "/decks/" + id,
                res.toString().hashCode(),
                new Date().getTime(),
                true);

        c.mDeck = new Deck(
                res.getString("name"),
                res.getString("description"),
                image,
                null);

        loadCards(id, c);
    }

    /**
     * This is used to load a card from the server into the Collector object.
     *
     * @param deckId the id of the Deck
     */
    private void loadCards(int deckId, Collector c)
            throws ExecutionException, InterruptedException, JSONException {

        RequestFuture<JSONArray> future = RequestFuture.newFuture();

        AuthJsonArrayRequest req = new AuthJsonArrayRequest(
                Request.Method.GET,
                SERVER_URL + "/decks/" + deckId + "/cards",
                null,
                future,
                future);

        mRequestQueue.add(req);

        JSONArray res = future.get();

        for (int i = 0; i < res.length(); i++) {

            JSONObject obj = res.getJSONObject(i);

            Card card = new Card(
                    obj.getString("name"),
                    c.mDeck,
                    i);

            c.mCards.add(card);

            loadAttributes(deckId, obj.getInt("id"), card, c);
            loadImages(deckId, obj.getInt("id"), card, c);
        }
    }

    /**
     * This is used to load the Attributes and AttributeValue of a Card into the
     * collector object.
     *
     * @param deckId the id of the associated deck
     * @param cardId the id of the associated card
     * @param card   the actual card object
     * @param c      the collector object
     */
    private void loadAttributes(int deckId, int cardId, Card card, Collector c)
            throws ExecutionException, InterruptedException, JSONException {

        RequestFuture<JSONArray> future = RequestFuture.newFuture();

        final String path = SERVER_URL + "/decks/" + deckId +
                "/cards/" + cardId + "/attributes";

        final AuthJsonArrayRequest req = new AuthJsonArrayRequest(
                Request.Method.GET,
                path,
                null,
                future,
                future);

        mRequestQueue.add(req);

        JSONArray res = future.get();

        for (int i = 0; i < res.length(); i++) {
            JSONObject obj = res.getJSONObject(i);

            Attribute attr = new Attribute(
                    obj.getString("name"),
                    obj.getString("unit"),
                    obj.getString("what_wins").equals("higher_wins"),
                    c.mDeck);

            boolean knownAttribute = false;
            for (Attribute a : c.mAttributes) {
                if (a.mName.equals(attr.mName)) {
                    attr = a;
                    knownAttribute = true;
                }
            }

            if (!knownAttribute) {
                c.mAttributes.add(attr);
            }

            final AttributeValue val = new AttributeValue(
                    (float) obj.getDouble("value"),
                    attr,
                    card);

            c.mAttributeValues.add(val);
        }
    }

    /**
     * This method is used to load all images of a card into the collector
     * object.
     *
     * @param deckId the id of the associated deck
     * @param cardId the id of the associated card
     * @param card   the card the images belong to
     * @param c      the collector object
     */
    private void loadImages(int deckId, int cardId, Card card, Collector c)
            throws ExecutionException, InterruptedException, JSONException {

        RequestFuture<JSONArray> future = RequestFuture.newFuture();

        final String path = SERVER_URL + "/decks/" + deckId +
                "/cards/" + cardId + "/images";

        final AuthJsonArrayRequest req = new AuthJsonArrayRequest(
                Request.Method.GET,
                path,
                null,
                future,
                future);

        mRequestQueue.add(req);

        JSONArray res = future.get();

        for (int i = 0; i < res.length(); i++) {
            JSONObject obj = res.getJSONObject(i);

            Image img = new Image(
                    obj.getString("image"),
                    obj.getString("description"));

            CardImage cardImage = new CardImage(
                    card,
                    img);

            c.mImages.add(img);
            c.mCardImages.add(cardImage);
        }
    }

    /**
     * This method will download the files the URLs of the Image objects point
     * to, store them in internal storage and alter the paths of the Image
     * objects accordingly. This method will block until all Images are
     * downloaded.
     *
     * @param images    the Image objects to be downloaded
     */
    public void loadImages(List<Image> images) throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(images.size());
        final String tag = "image";

        for (final Image i : images) {

            final String path = Uri.parse(i.mUri).getLastPathSegment();

            final FileRequest req = new FileRequest(
                    i.mUri,
                    path,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String uri) {

                            i.mUri = uri;
                            latch.countDown();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            mContext.deleteFile(path);
                            mRequestQueue.cancelAll(tag);
                            latch.countDown();
                        }
                    },
                    mContext);

            req.setTag(tag);

            mRequestQueue.add(req);
        }

        latch.await();
    }
}
