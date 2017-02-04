package de.in.uulm.map.quartett.rest;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.orm.SugarRecord;
import com.orm.SugarTransactionHelper;

import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.CardImage;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.DeckInfo;
import de.in.uulm.map.quartett.data.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Created by Jona on 04.02.2017.
 */

public class DownloadService extends IntentService {

    /**
     * An instance of this special class is passed to the deck loading methods
     * to collect all the data in a single object.
     */
    class Collector {

        Deck mDeck;
        DeckInfo mDeckInfo;

        ArrayList<Image> mImages = new ArrayList<>();
        ArrayList<CardImage> mCardImages = new ArrayList<>();
        ArrayList<Card> mCards = new ArrayList<>();
        ArrayList<Attribute> mAttributes = new ArrayList<>();
        ArrayList<AttributeValue> mAttributeValues = new ArrayList<>();

        public Collector() {

        }
    }

    /**
     * This is the request queue, which is used make request to the server.
     */
    private RequestQueue mRequestQueue;

    /**
     * Simple constructor. Calls super constructor.
     */
    public DownloadService() {

        super("QuartetDownloadService");
    }

    /**
     * This method is used to react to intents sent to the service. It will
     * download the deck with the id contained in the intent to the database in
     * the background.
     *
     * @param intent the Intent object that was passed to the service
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        mRequestQueue =
                Network.getInstance(getApplicationContext()).getRequestQueue();

        final int id = intent.getIntExtra("id", -1);

        if (id < 0) {
            return;
        }

        final Collector c = new Collector();

        try {
            getDeck(id, c);
            getImages(c.mImages);

            SugarTransactionHelper.doInTransaction(new SugarTransactionHelper.Callback() {

                @Override
                public void manipulateInTransaction() {

                    if (DeckInfo.find(DeckInfo.class, "m_source = ?",
                            c.mDeckInfo.mSource).size() > 0) {
                        return;
                    }

                    SugarRecord.saveInTx(c.mImages);
                    c.mDeckInfo.save();
                    c.mDeck.save();
                    SugarRecord.saveInTx(c.mAttributes);
                    SugarRecord.saveInTx(c.mCards);
                    SugarRecord.saveInTx(c.mAttributeValues);
                    SugarRecord.saveInTx(c.mCardImages);
                }
            });

            Toast.makeText(this, "Deck downloaded!", Toast.LENGTH_LONG);

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();

            for(Image i : c.mImages) {
                if(!i.mUri.contains(File.pathSeparator)) {
                    deleteFile(i.mUri);
                }
            }
        }
    }

    /**
     * Use this method to download a deck from the server.
     *
     * @param id the id of the deck to be downloaded
     */
    private void getDeck(int id, final Collector c)
            throws ExecutionException, InterruptedException {

        final RequestFuture<Deck> deckFuture = RequestFuture.newFuture();

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

            AttributesRequest attrReq = new AttributesRequest(
                    id,
                    c.mDeck,
                    card,
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

            ImagesRequest imagesReq = new ImagesRequest(
                    id,
                    card,
                    new Response.Listener<List<CardImage>>() {
                        @Override
                        public void onResponse(List<CardImage> images) {

                            c.mCardImages.addAll(images);

                            for (CardImage i : images) {
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
    }

    /**
     * Use this method to download all Images contained in a Deck to the
     * internal storage.
     *
     * @param images the list of Image objects to download
     * @throws InterruptedException
     */
    private void getImages(List<Image> images) throws InterruptedException {

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

                            mRequestQueue.cancelAll(tag);
                            latch.countDown();
                        }
                    },
                    getApplicationContext());

            req.setTag(tag);

            mRequestQueue.add(req);
        }

        latch.await();
    }
}
