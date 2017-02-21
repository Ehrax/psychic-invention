package de.in.uulm.map.quartett.rest;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.DeckInfo;
import de.in.uulm.map.quartett.data.Image;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Jona on 04.02.2017.
 *
 * This class encapsulates a request to the server to get a Deck object.
 */
public class DeckRequest extends AuthRequest<Deck> {

    private final Response.Listener<Deck> mListener;

    /**
     * Standard constructor, just calling super here.
     *
     * @param listener the error listener/callback
     */
    public DeckRequest(int deckId, Response.Listener<Deck> listener,
                       Response.ErrorListener errorListener) {

        super(Method.GET, URL + "/decks/" + deckId, errorListener);

        mListener = listener;
    }

    /**
     * This will construct the Deck object from the response.
     *
     * @param response the response from the server
     * @return a Response object containing either an error or the Deck object
     */
    @Override
    protected Response<Deck> parseNetworkResponse(NetworkResponse response) {

        if (response.statusCode >= 400) {
            return Response.error(new VolleyError(response));
        }

        try {
            JSONObject res = new JSONObject(new String(response.data));

            Image deckImage = new Image(
                    res.getString("image"),
                    res.getString("name"));

            DeckInfo deckInfo = new DeckInfo(
                    getUrl(),
                    res.toString().hashCode(),
                    new Date().getTime(),
                    DeckInfo.State.DISK);

            Deck deck = new Deck(
                    res.getString("name"),
                    res.getString("description"),
                    deckImage,
                    deckInfo);

            return Response.success(deck,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (JSONException e) {
            return Response.error(new VolleyError(e));
        }
    }

    /**
     * Called on the UI Thread when the request has finished processing will
     * call the listener/callback object.
     *
     * @param response the Response containing a Deck object
     */
    @Override
    protected void deliverResponse(Deck response) {

        mListener.onResponse(response);
    }
}
