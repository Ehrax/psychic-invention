package de.in.uulm.map.quartett.gallery;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.ActivityUtils;

/**
 * Created by maxka on 25.12.2016.
 */

public class GalleryActivity extends DrawerActivity {

    private GalleryPresenter mGalleryPresenter;
    /**
     * This ViewSwitcher simply replaces the current fragment with the given
     * one. This is necessary to change the fragments from the presenter.
     */
    private ViewSwitcher mViewSwitcher = new ViewSwitcher() {
        @Override
        public void switchToView(GalleryContract.View view) {

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    (Fragment) view, R.id.contentFrame);
            mGalleryPresenter = new GalleryPresenter(view,
                    getApplicationContext(), this);
            view.setPresenter(mGalleryPresenter);
        }
    };

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

        mGalleryPresenter = new GalleryPresenter(galleryFragment, this, mViewSwitcher);
        galleryFragment.setPresenter(mGalleryPresenter);
    }


    public interface ViewSwitcher {

        void switchToView(GalleryContract.View view);
    }

}
