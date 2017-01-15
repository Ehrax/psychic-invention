package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.util.AssetUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by maxka on 26.12.2016. Adapter for the RecyclerView in the gallery.
 * Setting the decks image and title for each row.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter
        .ViewHolder> {

    /*
    List of all available decks
     */
    private List<Deck> mDeckList;
    private GalleryFragment.GalleryItemListener mItemListener;

    private Context context;

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
            .GalleryItemListener itemListener, Context ctx) {

        mDeckList = deckList;
        mItemListener = itemListener;
        context = ctx;
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
     * method from the GalleryItemListener which calls the onDeckClicked method
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
            if (!currentDeck.mImage.mUri.contains("android_asset")) {
                viewHolder.mImageView.setImageURI(Uri.parse(currentDeck.mImage
                        .mUri));
            } else {
                setAssetDrawable(viewHolder, Uri.parse(currentDeck.mImage.mUri));
            }
        } else {
            Uri imageCardUri = Uri.parse(currentDeck.getCards()
                    .get(0).getCardImages().get(0).mImage.mUri);
            if (!imageCardUri.getPath().contains("android_asset")) {
                viewHolder.mImageView.setImageURI(imageCardUri);
            } else {
                setAssetDrawable(viewHolder, imageCardUri);
            }
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

    /**
     * this method is called by onBindViewHolder to set a imageviews source by
     * an asset uri.
     *
     * @param viewHolder the current ViewHolder for getting the ImageViews
     *                   reference
     * @param uri        the uri of the asset you want to load.
     */
    private void setAssetDrawable(ViewHolder viewHolder, Uri uri) {

        Drawable drawable = AssetUtils.getDrawableFromAssetUri
                (context, uri);
        if (drawable != null) {
            viewHolder.mImageView.setImageDrawable(drawable);
        } else {
            viewHolder.mImageView.setImageDrawable(context.getDrawable(R.drawable
                    .ic_cards_playing));
        }
    }

    @Override
    public int getItemCount() {

        return mDeckList.size();
    }
}
