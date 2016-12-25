package de.in.uulm.map.quartett.gallery;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.in.uulm.map.quartett.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by maxka on 25.12.2016.
 */

public class GalleryFragment extends Fragment implements GalleryContract.View {

    private GalleryContract.Presenter mPresenter;

    public static GalleryFragment newInstance() {

        return new GalleryFragment();
    }
    @Override
    public void setPresenter(@NonNull GalleryContract.Presenter presenter) {
        mPresenter= checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_gallery,container,false);
    }
}
