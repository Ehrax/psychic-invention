package de.in.uulm.map.quartett.gallery;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Deck;

import java.util.List;

/**
 * Created by maxka on 26.12.2016. Adapter for the RecyclerView in the gallery.
 * Setting the decks image and title for each row.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter
        .ViewHolder> {

    private List<Deck> mDeckList;
    private GalleryFragment.GalleryItemListener mItemListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextView;

        public ViewHolder(View v) {

            super(v);
            mImageView = (ImageView) v.findViewById(R.id.img_deck_gallery);
            mTextView = (TextView) v.findViewById(R.id.txt_deck_title_gallery);
        }
    }

    /**
     * Simple constructor to set the dataset for the gallery and a listener for
     * the click event
     *
     * @param deckList     A List of all Decks you want to insert into the
     *                     RecyclerView
     * @param itemListener A GalleryItemListener (inner interface from
     *                     GalleryFragment) to call methods from the presenter.
     */
    public GalleryAdapter(List<Deck> deckList, GalleryFragment
            .GalleryItemListener itemListener) {

        mDeckList = deckList;
        mItemListener = itemListener;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .gallery_row, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Called every time the RecyclerView is displayed. Loading the decks image
     * into the ImageView and the title into the TextView. This method adds an
     * onClickListener to the ImageView which calls the showDeckDetailView
     * method from the GalleryItemListener which calls the showDeckDetail method
     * from the GalleryPresenter.
     *
     * @param viewHolder the ViewHolder which holds the ImageView and the
     *                   TextView
     * @param position   the position of the current row
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final Deck currentDeck = mDeckList.get(position);
        if (currentDeck.mImage != null) {
            viewHolder.mImageView.setImageURI(currentDeck.mImage.mUri);
        } else {
            viewHolder.mImageView.setImageURI(currentDeck.mCards.get(0)
                    .mImages.get(0).mUri);
        }
        if (currentDeck.mTitle != null) {
            viewHolder.mTextView.setText(currentDeck.mTitle);
        } else {
            viewHolder.mTextView.setText(R.string.no_title);
        }

        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mItemListener.showDeckDetailView(currentDeck.getId());
            }
        });


    }

    @Override
    public int getItemCount() {

        return mDeckList.size();
    }
}
