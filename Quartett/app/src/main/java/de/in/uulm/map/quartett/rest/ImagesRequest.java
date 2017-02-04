package de.in.uulm.map.quartett.rest;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.CardImage;
import de.in.uulm.map.quartett.data.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jona on 04.02.2017.
 */

public class ImagesRequest extends AuthRequest<List<CardImage>> {

    final Card mCard;

    final Response.Listener<List<CardImage>> mListener;

    /**
     * Standard constructor, just calling super here.
     *
     * @param deckId   the id of the associated deck
     * @param card     the card the images belong to
     * @param listener the error listener/callback
     */
    public ImagesRequest(int deckId,
                         Card card,
                         Response.Listener<List<CardImage>> listener,
                         Response.ErrorListener errorListener) {

        super(Method.GET,
                URL + "/decks/" + deckId + "/cards/" + card.mServerId + "/images",
                errorListener);

        mCard = card;
        mListener = listener;
    }

    @Override
    protected Response<List<CardImage>> parseNetworkResponse(NetworkResponse response) {

        if (response.statusCode >= 400) {
            return Response.error(new VolleyError(response));
        }

        try {
            JSONArray res = new JSONArray(new String(response.data));

            ArrayList<CardImage> cardImages = new ArrayList<>();

            for (int i = 0; i < res.length(); i++) {
                JSONObject obj = res.getJSONObject(i);

                final Image img = new Image(
                        obj.getString("image"),
                        obj.getString("description"));

                cardImages.add(new CardImage(mCard, img));
            }

            return Response.success((List<CardImage>) cardImages,
                    HttpHeaderParser.parseCacheHeaders(response));

        } catch (JSONException e) {
            return Response.error(new VolleyError(e));
        }
    }

    /**
     * Called on the UI Thread when the request has finished processing. Will
     * call the listener/callback object.
     *
     * @param response the Response containing a List of Images
     */
    @Override
    protected void deliverResponse(List<CardImage> response) {

        mListener.onResponse(response);
    }
}
