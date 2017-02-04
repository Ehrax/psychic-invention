package de.in.uulm.map.quartett.rest;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jona on 04.02.2017.
 *
 * This class is used to encapsulate a cards request to the server.
 */
public class CardsRequest extends AuthRequest<List<Card>> {

    final Deck mDeck;

    final Response.Listener<List<Card>> mListener;

    /**
     * Standard constructor, just calling super here.
     *
     * @param deckId        the id of the deck to load the cards from
     * @param listener      the success listener
     * @param errorListener the error listener
     */
    public CardsRequest(int deckId,
                        Deck deck,
                        Response.Listener<List<Card>> listener,
                        Response.ErrorListener errorListener) {

        super(Method.GET, URL + "/decks/" + deckId + "/cards", errorListener);

        mDeck = deck;
        mListener = listener;
    }

    /**
     * This method is used to construct a List of Cards from the response.
     *
     * @param response the response from the network
     * @return a list of cards or error
     */
    @Override
    protected Response<List<Card>> parseNetworkResponse(NetworkResponse response) {

        if (response.statusCode >= 400) {
            return Response.error(new VolleyError(response));
        }

        try {
            JSONArray res = new JSONArray(new String(response.data));

            ArrayList<Card> cards = new ArrayList<>();

            for (int i = 0; i < res.length(); i++) {
                JSONObject obj = res.getJSONObject(i);
                Card card = new Card(obj.getString("name"), mDeck, i);
                card.mServerId = obj.getInt("id");
                cards.add(card);
            }

            return Response.success((List<Card>) cards,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (JSONException e) {
            return Response.error(new VolleyError(e));
        }
    }

    /**
     * Called on the UI Thread when the request has finished processing will
     * call the listener/callback object.
     *
     * @param response the Response containing a List of Cards
     */
    @Override
    protected void deliverResponse(List<Card> response) {

        mListener.onResponse(response);
    }
}
