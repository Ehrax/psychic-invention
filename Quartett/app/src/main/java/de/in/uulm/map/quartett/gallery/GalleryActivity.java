package de.in.uulm.map.quartett.gallery;

import android.os.Bundle;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.ActivityUtils;

/**
 * Created by maxka on 25.12.2016.
 */

public class GalleryActivity extends DrawerActivity {

    private GalleryPresenter mGalleryPresenter;

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        GalleryFragment galleryFragment = (GalleryFragment)
                getSupportFragmentManager
                ().findFragmentById(R.id.contentFrame);
        if(galleryFragment == null){
            galleryFragment = GalleryFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    galleryFragment,R.id.contentFrame);
        }

        mGalleryPresenter = new GalleryPresenter(galleryFragment,this);
        galleryFragment.setPresenter(mGalleryPresenter);
    }

}
