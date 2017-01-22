package de.in.uulm.map.quartett.rest;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.DeckInfo;
import de.in.uulm.map.quartett.data.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
                                        SERVER_URL + "/decks/" + obj.getInt ("id"),
                                        obj.toString().hashCode(),
                                        new Date().getTime());

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
     * This method should be used to retrieve a single Deck from the server.
     * Note that this will retrieve only the information necessary to construct
     * the Deck objects but will not download the contained Cards.
     *
     * @param id            the id of the Deck to be downloaded
     * @param deckListener  will be called when the Deck object is ready
     * @param errorListener will be called on any error
     */
    public void loadDeck(int id, final Response.Listener<Deck> deckListener,
                         final Response.ErrorListener errorListener) {

        AuthJsonObjectRequest jsonObjectRequest = new AuthJsonObjectRequest(
                Request.Method.GET,
                SERVER_URL + "/decks/" + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            final Image image = new Image(
                                    response.getString("image"),
                                    response.getString("name"));

                            final Deck deck = new Deck(
                                    response.getString("name"),
                                    response.getString("description"),
                                    image,
                                    null);

                            deckListener.onResponse(deck);
                        } catch (JSONException e) {
                            errorListener.onErrorResponse(new VolleyError(e));
                        }
                    }
                },
                errorListener);

        mRequestQueue.add(jsonObjectRequest);
    }
}
