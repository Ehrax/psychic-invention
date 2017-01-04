package de.in.uulm.map.quartett.gallery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Deck;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by maxka on 25.12.2016. This fragment uses a simple RecyclerView to
 * display all deck images + title
 */

public class GalleryFragment extends Fragment implements GalleryContract.View {

    private GalleryContract.Presenter mPresenter;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<Deck> deckList;

    /**
     * This listener method is called by the GalleryAdapter to switch to the
     * deckDetail fragment.
     */
    GalleryItemListener mItemListener = new GalleryItemListener() {
        @Override
        public void showDeckDetailView(long deckID) {

            mPresenter.showDeckDetail(deckID);
        }
    };

    public static GalleryFragment newInstance() {

        return new GalleryFragment();
    }

    @Override
    public void setPresenter(@NonNull GalleryContract.Presenter presenter) {

        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {

        super.onActivityCreated(savedInstance);

        deckList = mPresenter.populateDeckList();

        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id
                .recycler_view_gallery);

        /*set has fixed size to improve performance because the layout size
        will never change*/
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new GalleryAdapter(deckList, mItemListener,getContext());
        mRecyclerView.setAdapter(mAdapter);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    public interface GalleryItemListener {

        void showDeckDetailView(long deckID);
    }


}
