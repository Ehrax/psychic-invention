package de.in.uulm.map.quartett.rest;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.orm.SugarRecord;
import com.orm.SugarTransactionHelper;

import de.in.uulm.map.quartett.R;
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
     * This is the name of the broadcast action that is used to notify the app,
     * when a download has finished.
     */
    private static final String BROADCAST_ACTION =
            "de.in.uulm.map.quartett.rest.DOWNLOAD";

    /**
     * The ID of the Notification to show the progress of the download.
     */
    private static final int NOTIFICATION_ID = 1;

    /**
     * This is the request queue, which is used to make requests to the server.
     */
    private RequestQueue mRequestQueue;

    /**
     * This Notification Builder will be used for the Download Notifications.
     */
    private Notification.Builder builder;

    /**
     * This variable contains the download progress.
     */
    private float progress;

    /**
     * This variable stores the id of the deck that is currently downloaded.
     */
    private int id;

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

        progress = 0;

        mRequestQueue =
                Network.getInstance(getApplicationContext()).getRequestQueue();

        id = intent.getIntExtra("id", -1);

        if(builder == null) {
            builder = new Notification.Builder(this)
                    .setContentTitle("Quartett Download")
                    .setSmallIcon(R.drawable.ic_download)
                    .setProgress(100, 0, false);
        }

        builder.setContentText(intent.getStringExtra("title"));
        updateProgress();

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

        } catch (ExecutionException | InterruptedException | VolleyError e) {
            e.printStackTrace();

            for (Image i : c.mImages) {
                if (!i.mUri.contains(File.pathSeparator)) {
                    deleteFile(i.mUri);
                }
            }
        }

        Intent broadcastIntent = new Intent(BROADCAST_ACTION);
        broadcastIntent.putExtra("progress", 100);
        broadcastIntent.putExtra("id", id);
        sendBroadcast(broadcastIntent);

        closeNotification();
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

                            progress += 25.0f/(float)c.mCards.size();
                            updateProgress();

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

                            progress += 25.0f/(float)c.mCards.size();
                            updateProgress();

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
     */
    private void getImages(final List<Image> images) throws InterruptedException, VolleyError {

        final CountDownLatch latch = new CountDownLatch(images.size());
        final String tag = "image";
        final ArrayList<VolleyError> errors = new ArrayList<>();

        for (final Image i : images) {

            final String path = Uri.parse(i.mUri).getLastPathSegment();

            final FileRequest req = new FileRequest(
                    i.mUri,
                    path,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String uri) {

                            i.mUri = uri;
                            progress += 50.0f/(float)images.size();
                            updateProgress();
                            latch.countDown();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            mRequestQueue.cancelAll(tag);
                            latch.countDown();
                            errors.add(error);
                        }
                    },
                    getApplicationContext());

            req.setTag(tag);

            mRequestQueue.add(req);
        }

        latch.await();

        if (errors.isEmpty()) {
            return;
        }

        for (Image i : images) {
            if (!i.mUri.contains(File.pathSeparator)) {
                deleteFile(i.mUri);
            }
        }

        throw errors.get(0);
    }

    /**
     * This method is used to create or update the progress Notification.
     */
    public void updateProgress() {

        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        builder.setProgress(100, (int)progress, false);

        manager.notify(NOTIFICATION_ID, builder.build());

        Intent broadcastIntent = new Intent(BROADCAST_ACTION);
        broadcastIntent.putExtra("progress", (int)progress);
        broadcastIntent.putExtra("id", id);
        sendBroadcast(broadcastIntent);
    }

    /**
     * Use this method to remove the Notification, when the download is
     * finished.
     */
    public void closeNotification() {

        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        manager.cancel(NOTIFICATION_ID);
    }
}
