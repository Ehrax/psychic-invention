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
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.game.GameContract;

/**
 * Created by maxka on 25.12.2016. This fragment uses a simple RecyclerView to
 * display all deck images + title
 */
public class GalleryFragment extends Fragment implements GalleryContract.View {

    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    private GalleryContract.Presenter mPresenter;

    public static GalleryFragment newInstance() {

        return new GalleryFragment();
    }

    @Override
    public void setPresenter(@NonNull GalleryContract.Presenter presenter) {

        mPresenter = presenter;
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

    @Override
    public void showDownloadDialog(final Deck deck) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.gallery_download_title);
        builder.setMessage(R.string.gallery_download_msg);
        builder.setNegativeButton(R.string.gallery_btn_cancel, null);
        builder.setPositiveButton(R.string.gallery_btn_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        mPresenter.onDownloadDialogOk(deck);
                    }
                });

        builder.show();
    }

    public void showDeleteDialog(final Deck deck) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.gallery_delete_title);
        builder.setMessage(R.string.gallery_delete_msg);
        builder.setNegativeButton(R.string.gallery_btn_cancel, null);
        builder.setPositiveButton(R.string.gallery_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mPresenter.onDeleteDialogOk(deck);
            }
        });

        builder.show();
    }
}
