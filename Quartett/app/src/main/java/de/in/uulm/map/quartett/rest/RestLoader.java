package de.in.uulm.map.quartett.rest;

import android.content.Context;
import android.net.Uri;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;

import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.CardImage;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.DeckInfo;
import de.in.uulm.map.quartett.data.Image;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Created by Jona on 21.01.2017.
 */

public class RestLoader {

    private static final String SERVER_URL = "http://quartett.af-mba.dbis.info";

    private final Context mContext;

    private final RequestQueue mRequestQueue;

    /**
     * The constructor creates a request queue and initializes member
     * variables.
     *
     * @param context the current context, needed to create message queue
     */
    public RestLoader(Context context, RequestQueue queue) {

        mContext = context;
        mRequestQueue = queue;
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

        final Collector c = new Collector();

        RequestFuture<Deck> deckFuture = RequestFuture.newFuture();

        DeckRequest deckRequest = new DeckRequest(id, deckFuture, deckFuture);
        mRequestQueue.add(deckRequest);

        c.mDeck = deckFuture.get();
        c.mDeckInfo = c.mDeck.mDeckInfo;
        c.mImages.add(c.mDeck.mImage);

        RequestFuture<List<Card>> cardsFuture = RequestFuture.newFuture();

        CardsRequest cardsRequest =
                new CardsRequest(id, c.mDeck, cardsFuture, cardsFuture);
        mRequestQueue.add(cardsRequest);

        c.mCards.addAll(cardsFuture.get());

        final ArrayList<VolleyError> errors = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(c.mCards.size() * 2);

        for (Card card : c.mCards) {

            final String attrUrl = SERVER_URL + "/decks/" + id + "/cards/" +
                    card.mServerId + "/attributes";

            AttributesRequest attrReq = new AttributesRequest(
                    attrUrl,
                    card,
                    c.mDeck,
                    new Response.Listener<List<AttributeValue>>() {
                        @Override
                        public void onResponse(List<AttributeValue> attrs) {

                            for (AttributeValue a : attrs) {
                                int index = c.mAttributes.indexOf(a.mAttribute);
                                if (index < 0) {
                                    c.mAttributes.add(a.mAttribute);
                                } else {
                                    a.mAttribute = c.mAttributes.get(index);
                                }
                            }

                            c.mAttributeValues.addAll(attrs);

                            latch.countDown();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();
                            errors.add(error);
                            latch.countDown();
                        }
                    });

            mRequestQueue.add(attrReq);

            final String imagesUrl = SERVER_URL + "/decks/" + id + "/cards/" +
                    card.mServerId + "/images";

            ImagesRequest imagesReq = new ImagesRequest(
                    imagesUrl,
                    card,
                    new Response.Listener<List<CardImage>>() {
                        @Override
                        public void onResponse(List<CardImage> images) {

                            c.mCardImages.addAll(images);

                            for(CardImage i : images) {
                                c.mImages.add(i.mImage);
                            }

                            latch.countDown();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();
                            errors.add(error);
                            latch.countDown();
                        }
                    });

            mRequestQueue.add(imagesReq);
        }

        latch.await();

        return errors.isEmpty() ? c : null;
    }

    /**
     * This method will download the files the URLs of the Image objects point
     * to, store them in internal storage and alter the paths of the Image
     * objects accordingly. This method will block until all Images are
     * downloaded.
     *
     * @param images the Image objects to be downloaded
     */
    public void loadImages(List<Image> images) throws InterruptedException {

        final CountDownLatch latch = new CountDownLatch(images.size());
        final String tag = "image";
        final ArrayList<Image> saved = new ArrayList<>();

        for (final Image i : images) {

            final String path = Uri.parse(i.mUri).getLastPathSegment();

            final FileRequest req = new FileRequest(
                    i.mUri,
                    path,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String uri) {

                            i.mUri = uri;
                            saved.add(i);
                            latch.countDown();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            mRequestQueue.cancelAll(tag);
                            latch.countDown();
                        }
                    },
                    mContext);

            req.setTag(tag);

            mRequestQueue.add(req);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            for (Image s : saved) {
                mContext.deleteFile(s.mUri);
            }
            throw e;
        }

        if (saved.size() != images.size()) {
            for (Image s : saved) {
                mContext.deleteFile(s.mUri);
            }
        }
    }
}
