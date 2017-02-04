package de.in.uulm.map.quartett.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.rest.DeckDownloadTask;
import de.in.uulm.map.quartett.rest.DecksRequest;
import de.in.uulm.map.quartett.rest.Network;
import de.in.uulm.map.quartett.rest.RestLoader;
import de.in.uulm.map.quartett.util.ActivityUtils;

import java.util.List;

/**
 * Created by maxka on 25.12.2016.
 */

public class GalleryActivity extends DrawerActivity implements GalleryContract.Backend {

    private GalleryPresenter mGalleryPresenter;

    private RestLoader mRestLoader;

    private GalleryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mRestLoader = new RestLoader(this,
                Network.getInstance(getApplicationContext()).getRequestQueue());
        mAdapter = new GalleryAdapter(this, mRestLoader);

        GalleryFragment galleryFragment = (GalleryFragment)
                getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (galleryFragment == null) {
            galleryFragment = GalleryFragment.newInstance();
            galleryFragment.setAdapter(mAdapter);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    galleryFragment, R.id.contentFrame);
        }

        mGalleryPresenter =
                new GalleryPresenter(this, galleryFragment, this, mAdapter);

        galleryFragment.setPresenter(mGalleryPresenter);
        mAdapter.setPresenter(mGalleryPresenter);

        mGalleryPresenter.start();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    /**
     * This is used to change the currently displayed fragment. Part of the
     * Backend Interface.
     *
     * @param view the new view that should be displayed
     */
    @Override
    public void switchToView(GalleryContract.View view) {

        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                (Fragment) view, R.id.contentFrame);
        mGalleryPresenter = new GalleryPresenter(this, view, this, mAdapter);
        view.setPresenter(mGalleryPresenter);
    }

    /**
     * Part of the Backend Interface. Is used to switch to another activity.
     *
     * @param intent the intent with which to start the other activity.
     */
    @Override
    public void startActivity(Intent intent) {

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this);

        super.startActivity(intent, options.toBundle());
    }

    /**
     * Use this method to asynchronously load an image over the Network.
     *
     * @param url       the url of the image to be loaded
     * @param imageView the image view in which the image will be placed
     */
    @Override
    public void loadServerImage(String url, ImageView imageView) {

        Network.getInstance(getApplicationContext())
                .getImageLoader()
                .get(url, ImageLoader.getImageListener(
                                imageView, R.drawable.empty, R.drawable.empty));
    }

    /**
     * Part of the Backend interface. Is use to load the decks from the
     * Database. Will be called by the presenter.
     */
    @Override
    public void loadDecks() {

        mGalleryPresenter.onDeckLoaded(Deck.listAll(Deck.class));
    }

    /**
     * Part of the Backend interface. This is used to load the decks from the
     * Server. Will be called by the presenter.
     */
    @Override
    public void loadServerDecks() {

        final RequestQueue queue =
                Network.getInstance(getApplicationContext()).getRequestQueue();

        final DecksRequest request = new DecksRequest(
                new Response.Listener<List<Deck>>() {
                    @Override
                    public void onResponse(List<Deck> decks) {

                        mGalleryPresenter.onDeckLoaded(decks);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        // TODO: display a hint that there was an error
                    }
                });

        queue.add(request);
    }

    /**
     * This method is used to download a Deck into the local database. Part of
     * the Backend interface, will be called by the presenter.
     *
     * @param deck the deck to be downloaded
     */
    @Override
    public void downloadDeck(final Deck deck) {

        int id = Integer.parseInt(
                Uri.parse(deck.mDeckInfo.mSource).getLastPathSegment());

        new DeckDownloadTask(id, mRestLoader, new DeckDownloadTask.Callback() {
            @Override
            public void onFinished(Deck newDeck) {

                mGalleryPresenter.onDeckDownloaded(deck, newDeck);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
