package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.util.AssetUtils;
import de.in.uulm.map.quartett.util.AsyncImageLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        Collections.sort(mDeckList, new Comparator<Deck>() {
            @Override
            public int compare(Deck o1, Deck o2) {

                return o1.mTitle.compareTo(o2.mTitle);
            }
        });

        notifyDataSetChanged();
    }

    /**
     * Part of the model interface. Forces the view to update a single data
     * element given the position in the item list.
     * @param position
     */
    public void update(int position, Object payload) {

        notifyItemChanged(position, payload);
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

        if(currentDeck.mImage.mUri == null || currentDeck.mImage.mUri.isEmpty()) {
            viewHolder.mImageView.setImageResource(R.drawable.empty);
        }

        new AsyncImageLoader(
                currentDeck.mImage.mUri,
                new WeakReference<>(viewHolder.mImageView),
                context).execute();

        viewHolder.mTextView.setText(
                currentDeck.mTitle == null ? "" : currentDeck.mTitle);

        viewHolder.mDescriptionView.setText(
                currentDeck.mDescription == null ? "" : currentDeck.mDescription);

        viewHolder.mDownloadIcon.setVisibility(
                currentDeck.mDeckInfo.mProgress == 0
                        ? View.VISIBLE : View.GONE);

        viewHolder.mDownloadProgress.setVisibility(
                currentDeck.mDeckInfo.mProgress != 0 &&
                        currentDeck.mDeckInfo.mProgress < 100
                        ? View.VISIBLE : View.GONE);

        viewHolder.mDownloadProgress.setIndeterminate(
                currentDeck.mDeckInfo.mProgress == 1);

        viewHolder.mDownloadProgress.setProgress(
                currentDeck.mDeckInfo.mProgress);

        viewHolder.mBtnDelete.setVisibility(
                currentDeck.mDeckInfo.mSource.contains("http://") &&
                        currentDeck.mDeckInfo.mProgress == 100
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
     * This function is needed to accept partial changed to a row item.
     * If possible the row will only be updated in part, which is more
     * efficient.
     *
     * @param holder the ViewHolder
     * @param position the position of the underlying data element in the list
     * @param payloads the partial change (here a Integer object with progress)
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {

        if(payloads.isEmpty()){
            onBindViewHolder(holder, position);
            return;
        }

        Object p = payloads.get(0);
        if(p instanceof Integer) {
            holder.mDownloadProgress.setProgress((Integer) p);
            holder.mDownloadProgress.setIndeterminate(false);
        }

        Log.d("Test", "" + payloads.size());
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
