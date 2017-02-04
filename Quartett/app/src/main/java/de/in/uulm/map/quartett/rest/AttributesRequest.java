package de.in.uulm.map.quartett.rest;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jona on 04.02.2017.
 */

public class AttributesRequest extends AuthRequest<List<AttributeValue>> {

    final Card mCard;

    final Deck mDeck;

    final Response.Listener<List<AttributeValue>> mListener;

    /**
     * Standard constructor, just calling super here and setting some member
     * variables.
     *
     * @param deckId        the id of the associated deck
     * @param deck          the associated deck
     * @param card          the associated card
     * @param errorListener the error listener/callback
     */
    public AttributesRequest(int deckId,
                             Deck deck,
                             Card card,
                             Response.Listener<List<AttributeValue>> listener,
                             Response.ErrorListener errorListener) {

        super(Method.GET,
                URL + "/decks/" + deckId + "/cards/" + card.mServerId + "/attributes",
                errorListener);

        mCard = card;
        mDeck = deck;
        mListener = listener;
    }

    /**
     * This will construct Attributes and AttributeValues from the response.
     *
     * @param response the response obtained from the network
     * @return a Response containing a List of AttributeValues of an VolleyError
     */
    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {

        if (response.statusCode >= 400) {
            return Response.error(new VolleyError(response));
        }

        try {
            JSONArray res = new JSONArray(new String(response.data));
            ArrayList<AttributeValue> values = new ArrayList<>();

            for (int i = 0; i < res.length(); i++) {
                JSONObject obj = res.getJSONObject(i);

                final Attribute attr = new Attribute(
                        obj.getString("name"),
                        obj.getString("unit"),
                        obj.getString("what_wins").equals("higher_wins"),
                        mDeck);

                values.add(new AttributeValue(
                        (float) obj.getDouble("value"),
                        attr,
                        mCard));
            }

            return Response.success((List<AttributeValue>) values,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (JSONException e) {
            return Response.error(new VolleyError(e));
        }
    }

    /**
     * Called on the UI Thread when the request has finished processing will
     * call the listener/callback object.
     *
     * @param response the Response containing a List of AttributeValues
     */
    @Override
    protected void deliverResponse(List<AttributeValue> response) {

        mListener.onResponse(response);
    }
}
