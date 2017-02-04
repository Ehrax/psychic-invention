package de.in.uulm.map.quartett.rest;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.DeckInfo;
import de.in.uulm.map.quartett.data.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jona on 04.02.2017.
 */

public class DecksRequest extends AuthRequest<List<Deck>> {

    final Response.Listener<List<Deck>> mListener;

    /**
     * Standard constructor, just calling super here.
     *
     * @param listener the error listener/callback
     */
    public DecksRequest(Response.Listener<List<Deck>> listener,
                        Response.ErrorListener errorListener) {

        super(Method.GET, URL + "/decks", errorListener);

        mListener = listener;
    }

    /**
     * This method is used to generate a List of Decks from the Server
     * Response.
     *
     * @param response the response from the server
     * @return a response containing an error or a List of Decks
     */
    @Override
    protected Response<List<Deck>> parseNetworkResponse(NetworkResponse response) {

        if (response.statusCode >= 400) {
            return Response.error(new VolleyError(response));
        }

        try {
            JSONArray res = new JSONArray(new String(response.data));
            ArrayList<Deck> decks = new ArrayList<>();

            for (int i = 0; i < res.length(); i++) {
                final JSONObject obj = res.getJSONObject(i);

                final Image image = new Image(
                        obj.getString("image"),
                        obj.getString("name"));

                final DeckInfo deckInfo = new DeckInfo(
                        URL + "/decks/" + obj.getInt("id"),
                        obj.toString().hashCode(),
                        new Date().getTime(),
                        DeckInfo.State.SERVER);

                final Deck deck = new Deck(
                        obj.getString("name"),
                        obj.getString("description"),
                        image,
                        deckInfo);

                decks.add(deck);
            }

            return Response.success((List<Deck>) decks,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (JSONException e) {
            return Response.error(new VolleyError(e));
        }
    }

    /**
     * Called on the UI Thread when the request has finished processing will
     * call the listener/callback object.
     *
     * @param response the Response containing a List of Decks
     */
    @Override
    protected void deliverResponse(List<Deck> response) {

        mListener.onResponse(response);
    }
}
