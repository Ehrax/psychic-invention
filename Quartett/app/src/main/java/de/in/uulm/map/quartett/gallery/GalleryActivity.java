package de.in.uulm.map.quartett.gallery;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.method.BaseKeyListener;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.ActivityUtils;

/**
 * Created by maxka on 25.12.2016.
 */


public class GalleryActivity extends DrawerActivity implements GalleryContract.Backend {

    private GalleryPresenter mGalleryPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        GalleryFragment galleryFragment = (GalleryFragment)
                getSupportFragmentManager
                        ().findFragmentById(R.id.contentFrame);
        if (galleryFragment == null) {
            galleryFragment = GalleryFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    galleryFragment, R.id.contentFrame);
        }


        mGalleryPresenter = new GalleryPresenter(galleryFragment, this, this);


        galleryFragment.setPresenter(mGalleryPresenter);
    }

<<<<<<< HEAD
    public interface ViewSwitcher {
=======
    @Override
    public void onBackPressed() {

        //Cancel deck initialization if there is one running
        if (DeckFragment.deckInitializer != null) {
            DeckFragment.deckInitializer.cancel(true);
        }
        super.onBackPressed();
    }

    @Override
    public void switchToView(GalleryContract.View view) {
>>>>>>> develop

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
}
