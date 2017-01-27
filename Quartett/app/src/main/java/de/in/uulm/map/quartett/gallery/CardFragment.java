package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import android.os.AsyncTask;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.CardImage;

import de.in.uulm.map.quartett.game.GameContract;

import de.in.uulm.map.quartett.data.Image;

import de.in.uulm.map.quartett.util.AssetUtils;
import de.in.uulm.map.quartett.views.viewpagerindicator.CirclePageIndicator;

import java.io.File;
import java.util.List;

/**
 * Created by maxka on 25.12.2016. Represents a single card. Used to show cards
 * in the deck detail view.
 */
public class CardFragment extends Fragment {

    private GameContract.Presenter mGamePresenter;
    private GalleryContract.Presenter mPresenter;
    private AsyncCardInitializer mInitializer;

    private long mDeckId;
    private int mPosition;
    private boolean mIsInGame = false;

    /**
     * This click listener is used to handle the attribute click event from the
     * AttributeAdapter.
     */
    private CardFragment.AttributeClickListener mAttributeListener = new AttributeClickListener() {
        @Override
        public void OnItemClick(Attribute chosenAttribute) {

            if (mGamePresenter != null) {
                if (mGamePresenter.getCurrentGameState().mIsUsersTurn) {
                    mGamePresenter.chooseAttribute(chosenAttribute);
                } else {
                    Snackbar.make(getView(), R.string.not_your_turn,
                            Snackbar
                                    .LENGTH_SHORT).show();
                }
            }
        }
    };

    public static CardFragment newInstance() {

        return new CardFragment();
    }

    /**
     * Use this method to set the cards presenter.
     *
     * @param presenter the presenter to be used
     */
    public void setPresenter(GalleryContract.Presenter presenter) {

        mPresenter = presenter;
    }

    /**
     * Use this method to set the deck id the card content will be load from.
     *
     * @param deckId the deckId to load from
     */
    public void setDeckId(long deckId) {

        mDeckId = deckId;
    }

    /**
     * Use this method to set the position of the card, which will be displayed
     * in this fragment.
     *
     * @param position the position of the card in the deck
     */
    public void setPosition(int position) {

        mPosition = position;
    }

    /**
     * This method is used to set the GamePresenter. The GamePresenter is used
     * to set click listeners for the attributes. So the GamePresenter can
     * handle the game logic.
     *
     * @param presenter the GamePresenter
     */
    public void setGamePresenter(GameContract.Presenter presenter) {

        mGamePresenter = presenter;

    }

    /**
     * This method is used to tell the card fragment if is displayed in a game
     * view or somewhere else. Setting this will activate different load
     * behavior and alter the way in which the card is displayed.
     */
    public void setIsInGame(boolean isInGame) {

        mIsInGame = isInGame;
    }

    /**
     * Creating the Card. Loading the Images and Attributes into the Layout.
     *
     * @param inflater           see onCreateView in Fragment class
     * @param container          see onCreateView in Fragment class
     * @param savedInstanceState see onCreateView in Fragment class
     * @return see onCreateView in Fragment class
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card, container, false);

        if (mIsInGame) {
            view.setBackgroundResource(R.drawable.card_border_game);
        }

        return view;
    }

    /**
     * This method is called when the view has been fully initialized. It is
     * used to start loading the data into the card.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        if (mInitializer != null) {
            mInitializer.cancel(true);
        }

        mInitializer = new AsyncCardInitializer(mDeckId, mPosition);
        mInitializer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    /**
     * This Adapter populates the cards images into a viewpager.
     */
    private class ImagePagerAdapter extends PagerAdapter {

        private List<CardImage> mImages;
        private Context mContext;

        /**
         * Setting the Context and all images
         *
         * @param ctx    context where the viewpager runs in
         * @param images array of drawables representing the images of the card
         */
        public ImagePagerAdapter(Context ctx, List<CardImage> images) {

            mContext = ctx;
            mImages = images;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == ((ImageView) object);
        }

        @Override
        public int getCount() {

            return mImages.size();
        }

        /**
         * This Method is used to instantiate a ImageView per Image and load an
         * image into it.
         *
         * @param container the viewpager
         * @param position  the position of the current element
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            final Image img = mImages.get(position).mImage;

            ImageView imgView = new ImageView(mContext);
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (img.mUri.contains("android_asset")) {
                Drawable drawable = AssetUtils.getDrawableFromAssetUri(
                        mContext, Uri.parse(img.mUri));
                imgView.setImageDrawable(drawable);
            } else {
                imgView.setImageURI(Uri.parse(mContext.getFilesDir() +
                        File.separator + img.mUri));
            }

            container.addView(imgView, 0);

            imgView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (mPresenter != null) {
                        mPresenter.onImageLongClicked(img);
                    } else {
                        mGamePresenter.onImageLongClicked(img);
                    }
                    return false;
                }
            });

            return imgView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            ((ViewPager) container).removeView((ImageView) object);
        }
    }

    class AsyncCardInitializer extends AsyncTask<Void, Void, Void> {

        private final long mDeckId;
        private final int mPosition;

        private String mTitle;
        private List<AttributeValue> mAttributeValues;
        private List<CardImage> mCardImages;

        AsyncCardInitializer(long deckId, int position) {

            mDeckId = deckId;
            mPosition = position;

        }

        /**
         * loading the card from the DB.
         */
        @Override
        protected Void doInBackground(Void... voids) {

            Card card = mPresenter != null ? mPresenter.getCard(mDeckId,
                    mPosition) : mGamePresenter.getCard(mDeckId, mPosition);

            mTitle = card.mTitle;
            mAttributeValues = card.getAttributeValues();
            mCardImages = card.getCardImages();

            return null;
        }

        /**
         * Setting the Cards Image and Title as well as circle image indicator
         * if there are multiple images. Also initialising the recycler view
         * which is holding the attributes.
         */
        @Override
        protected void onPostExecute(Void aVoid) {

            View view = CardFragment.this.getView();

            if (isCancelled() || view == null) {
                return;
            }
            view.findViewById(R.id.txt_card_title).setVisibility(View.VISIBLE);
            view.findViewById(R.id.progress_bar_card).setVisibility(View.GONE);

            //initializing the viewpager for multiple image support
            ViewPager viewPagerImages = (ViewPager) view.findViewById(R.id
                    .view_pager_img_card);

            ViewGroup.LayoutParams params = viewPagerImages.getLayoutParams();
            params.height = calculateImageHeight(view);
            viewPagerImages.setLayoutParams(params);

            ImagePagerAdapter imgPagerAdapter =
                    new ImagePagerAdapter(getContext(), mCardImages);
            viewPagerImages.setAdapter(imgPagerAdapter);
            /*Setting the circles indicating how many images the card has only if
             it has more than one image.*/
            if (mCardImages.size() > 1) {
                CirclePageIndicator indicator = (CirclePageIndicator) view
                        .findViewById(R.id.page_indicator_card);
                indicator.setViewPager(viewPagerImages);
            }

            TextView titleTextView = (TextView) view.findViewById(R.id
                    .txt_card_title);
            titleTextView.setText(mTitle);


            RecyclerView recyclerViewAttributes = (RecyclerView) view
                    .findViewById(R.id.recycler_view_card_attr);
            LinearLayoutManager layoutManager = new LinearLayoutManager
                    (getContext());
            recyclerViewAttributes.setLayoutManager(layoutManager);


            RecyclerView.Adapter adapter = new AttributeAdapter
                    (mAttributeValues, getContext(), mAttributeListener,
                            recyclerViewAttributes.getHeight());
            recyclerViewAttributes.addItemDecoration(new
                    DividerItemDecoration(recyclerViewAttributes.getContext()
                    , layoutManager.getOrientation()));
            recyclerViewAttributes.setAdapter(adapter);

        }

        private int calculateImageHeight(View view) {

            LinearLayout linearLayoutRoot = (LinearLayout) view.findViewById(R
                    .id.lin_layout_card);
            int cardHeight = linearLayoutRoot.getHeight();

            return cardHeight * 5/12;


        }
    }

    interface AttributeClickListener {


        void OnItemClick(Attribute chosenAttribute);

    }


}

