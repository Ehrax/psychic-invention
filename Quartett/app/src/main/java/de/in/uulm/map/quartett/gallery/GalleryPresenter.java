package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by maxka on 25.12.2016.
 */

public class GalleryPresenter implements GalleryContract.Presenter {

    @NonNull
    private final GalleryContract.View mView;
    private final Context ctx;

    public GalleryPresenter(@NonNull GalleryContract.View galleryView, Context ctx) {

        mView = galleryView;
        this.ctx = ctx;
    }

    @Override
    public void start() {

    }
}
