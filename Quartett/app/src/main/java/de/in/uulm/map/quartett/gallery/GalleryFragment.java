package de.in.uulm.map.quartett.gallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orm.dsl.NotNull;

import de.in.uulm.map.quartett.R;

/**
 * Created by maxka on 25.12.2016. This fragment uses a simple RecyclerView to
 * display all deck images + title
 */
public class GalleryFragment extends Fragment implements GalleryContract.View {

    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    public static GalleryFragment newInstance() {

        return new GalleryFragment();
    }

    @Override
    public void setPresenter(@NonNull GalleryContract.Presenter presenter) {

    }

    public void setAdapter(@NotNull GalleryAdapter adapter) {

        mAdapter = adapter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {

        super.onActivityCreated(savedInstance);

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id
                .recycler_view_gallery);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }
}
