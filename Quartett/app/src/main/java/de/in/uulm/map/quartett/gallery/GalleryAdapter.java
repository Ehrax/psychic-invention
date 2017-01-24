package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.DeckInfo;
import de.in.uulm.map.quartett.util.AssetUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by maxka on 26.12.2016. Adapter for the RecyclerView in the gallery.
 * Setting the decks image and title for each row.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>
        implements GalleryContract.Model {

    final private Context context;

    final private ArrayList<Deck> mDeckList;

    private GalleryContract.Presenter mPresenter;

    /**
     * Simple constructor to initialize member variables.
     *
     * @param ctx the current application context
     */
    public GalleryAdapter(Context ctx) {

        mDeckList = new ArrayList<>();
        context = ctx;
    }

    /**
     * Use this method to set the presenter.
     *
     * @param presenter the presenter to be set
     */
    public void setPresenter(GalleryContract.Presenter presenter) {

        mPresenter = presenter;
    }

    /**
     * This is part of the Model Interface. Should be called when something has
     * changed in the underlying data.
     */
    @Override
    public void update() {

        notifyDataSetChanged();
    }

    /**
     * This is part of the Model interface. But as the Gallery Adapter is part
     * of both (model and view) we implement it here. This gives access to the
     * deck list of the model
     *
     * @return the list of all decks in the adapter
     */
    @Override
    public ArrayList<Deck> getDecks() {

        return mDeckList;

    }

    /**
     * This will be called when a view holder object must be created.
     *
     * @param parent   the paren view group in which the layout will be
     *                 inflated
     * @param viewType the type of the view
     * @return a new ViewHolder instance
     */
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.gallery_row, parent, false);

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
            if (currentDeck.mImage.mUri.contains("http")) {
                mPresenter.loadServerImage(currentDeck.mImage.mUri,
                        viewHolder.mImageView);
            } else if (currentDeck.mImage.mUri.contains("android_asset")) {
                setAssetDrawable(viewHolder, Uri.parse(currentDeck.mImage.mUri));
            } else {
                viewHolder.mImageView.setImageURI(Uri.parse(
                        context.getFilesDir() + File.separator +
                                currentDeck.mImage.mUri));
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

        viewHolder.mTextView.setText(
                currentDeck.mTitle == null ? "" : currentDeck.mTitle);

        viewHolder.mDescriptionView.setText(
                currentDeck.mDescription == null ? "" : currentDeck.mDescription);

        viewHolder.mDownloadIcon.setVisibility(
                currentDeck.mDeckInfo.mState == DeckInfo.State.SERVER
                        ? View.VISIBLE : View.GONE);

        viewHolder.mDownloadProgress.setVisibility(
                currentDeck.mDeckInfo.mState == DeckInfo.State.DOWNLOADING
                        ? View.VISIBLE : View.GONE);

        viewHolder.mBtnDelete.setVisibility(
                currentDeck.mDeckInfo.mSource.contains("http://") &&
                        currentDeck.mDeckInfo.mState == DeckInfo.State.DISK
                        ? View.VISIBLE : View.GONE);

        viewHolder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPresenter.onDeleteDeckClicked(currentDeck);
            }
        });

        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPresenter.onDeckClicked(currentDeck);
            }
        });
    }

    /**
     * this method is called by onBindViewHolder to set a image views source by
     * an asset uri.
     *
     * @param viewHolder the current ViewHolder for getting the ImageViews
     *                   reference
     * @param uri        the uri of the asset you want to load.
     */
    private void setAssetDrawable(ViewHolder viewHolder, Uri uri) {

        Drawable drawable = AssetUtils.getDrawableFromAssetUri(context, uri);

        if (drawable != null) {
            viewHolder.mImageView.setImageDrawable(drawable);
        } else {
            viewHolder.mImageView.setImageDrawable(
                    context.getDrawable(R.drawable.ic_cards_playing));
        }
    }

    /**
     * This method will return the current count of items contained in the
     * adapter.
     *
     * @return count of items in the adapter
     */
    @Override
    public int getItemCount() {

        return mDeckList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImageView;
        public TextView mTextView;
        public TextView mDescriptionView;
        public ImageView mDownloadIcon;
        public ProgressBar mDownloadProgress;
        public ImageButton mBtnDelete;

        public ViewHolder(View v) {

            super(v);
            mImageView = (ImageView) v.findViewById(R.id.img_deck_gallery);
            mTextView = (TextView) v.findViewById(R.id.txt_deck_title_gallery);
            mDescriptionView = (TextView) v.findViewById(R.id.txt_deck_desc_gallery);
            mDownloadIcon = (ImageView) v.findViewById(R.id.download_icon);
            mDownloadProgress = (ProgressBar) v.findViewById(R.id.download_progress);
            mBtnDelete = (ImageButton) v.findViewById(R.id.btn_delete_deck);
        }
    }
}
