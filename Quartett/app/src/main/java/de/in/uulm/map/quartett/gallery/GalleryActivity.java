package de.in.uulm.map.quartett.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.rest.DeckDownloadTask;
import de.in.uulm.map.quartett.rest.RestLoader;
import de.in.uulm.map.quartett.util.ActivityUtils;

import java.util.List;

/**
 * Created by maxka on 25.12.2016.
 */

public class GalleryActivity extends DrawerActivity implements GalleryContract.Backend {

    private GalleryPresenter mGalleryPresenter;

    private RestLoader mRestLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final GalleryMode mode =
                (GalleryMode) getIntent().getSerializableExtra("mode");

        final List<Deck> decks = Deck.listAll(Deck.class);
        final GalleryAdapter adapter = new GalleryAdapter(decks, this);

        mRestLoader = new RestLoader(this);

        if (mode != GalleryMode.CHOOSE) {
            mRestLoader.loadAllDecks(new Response.Listener<List<Deck>>() {
                @Override
                public void onResponse(List<Deck> response) {

                    for (Deck d : response) {
                        decks.add(d);
                    }

                    adapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    // display a snack bar or something ...
                }
            });
        }

        GalleryFragment galleryFragment = (GalleryFragment)
                getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (galleryFragment == null) {
            galleryFragment = GalleryFragment.newInstance();
            galleryFragment.setAdapter(adapter);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    galleryFragment, R.id.contentFrame);
        }

        mGalleryPresenter = new GalleryPresenter(galleryFragment, this, this);
        galleryFragment.setPresenter(mGalleryPresenter);
        adapter.setPresenter(mGalleryPresenter);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void switchToView(GalleryContract.View view) {

        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                (Fragment) view, R.id.contentFrame);
        mGalleryPresenter = new GalleryPresenter(view,
                getApplicationContext(), this);
        view.setPresenter(mGalleryPresenter);
    }

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

        mRestLoader.loadImage(url, imageView, R.drawable.empty, R.drawable.empty);
    }

    /**
     * This method is used to download a Deck into the local database. Part of
     * the Backend interface.
     *
     * @param deck the deck to be downloaded
     */
    @Override
    public void downloadDeck(Deck deck) {

        int id = Integer.parseInt(
                Uri.parse(deck.mDeckInfo.mSource).getLastPathSegment());

        new DeckDownloadTask(id, mRestLoader).execute();
    }
}
