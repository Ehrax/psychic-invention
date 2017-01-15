package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.CardImage;
import de.in.uulm.map.quartett.data.Image;
import de.in.uulm.map.quartett.util.AssetUtils;
import de.in.uulm.map.quartett.views.viewpagerindicator.CirclePageIndicator;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by maxka on 25.12.2016. Represents a single card. Used to show cards
 * in the deck detail view.
 */
public class CardFragment extends Fragment {

    private GalleryContract.Presenter mPresenter;
    private AsyncCardInitializer mInitializer;

    private long mDeckId;
    private int mPosition;

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

        return inflater.inflate(R.layout.fragment_card, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        if (mInitializer != null) {
            mInitializer.cancel(true);
        }

        mInitializer = new AsyncCardInitializer(mDeckId, mPosition);
        mInitializer.execute();
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
            Drawable drawable = null;

            if (img.mUri.contains("android_asset")) {
                drawable = AssetUtils.getDrawableFromAssetUri(
                        mContext, Uri.parse(img.mUri));
            } else {
                try {
                    InputStream inputStream = getActivity()
                            .getContentResolver()
                            .openInputStream((Uri.parse(img.mUri)));
                    drawable = Drawable.createFromStream(inputStream, img.mUri);
                } catch (FileNotFoundException e) {
                    drawable = getResources().getDrawable(
                            R.drawable.ic_cards_playing, null);
                }
            }

            ImageView imgView = new ImageView(mContext);
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgView.setImageDrawable(drawable);
            ((ViewPager) container).addView(imgView, 0);

            imgView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    mPresenter.onImageLongClicked(img);
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

        @Override
        protected Void doInBackground(Void... voids) {

            Card card = mPresenter.getCard(mDeckId, mPosition);

            mTitle = card.mTitle;
            mAttributeValues = card.getAttributeValues();
            mCardImages = card.getCardImages();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            View view = CardFragment.this.getView();

            if (isCancelled() || view == null) {
                return;
            }

            //initializing the viewpager for multiple image support
            ViewPager viewPagerImages = (ViewPager) view.findViewById(R.id
                    .view_pager_img_card);

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
            TableLayout tableLayoutAttributes = (TableLayout) view.findViewById
                    (R.id.table_layout_card_attr);
            titleTextView.setText(mTitle);

            //building the attribute layout
            for (int i = 0; i < mAttributeValues.size(); i++) {
                AttributeValue currentAttrValue = mAttributeValues.get(i);

                /*this table row holds the attribute title as well as the
                 attribute value*/
                TableRow tableRow = new TableRow(getContext());
                tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup
                        .LayoutParams.MATCH_PARENT, 0, 1));
                tableRow.setBackgroundResource(R.drawable.table_border);

                /*row background color appears as bottom border because the
                 TextViews has darker background color and they are matching
                 the tableRow except the tableRows padding.*/
                //tableRow.setBackgroundColor(getResources().getColor(R
                //       .color.colorTableDivider));
                /*setting bottom padding to one except for the last attribute to
                 define a bottom border*/
                if (i < mAttributeValues.size() - 1) {
                    tableRow.setPaddingRelative(0, 0, 0, 1);
                }

                tableRow.setGravity(Gravity.CENTER_VERTICAL);

                ImageView winIndicator = new ImageView(getContext(),
                        null, 0, R.style.ImageViewCardWinIndicator);
                winIndicator.setImageResource(R.drawable.ic_card_win_indicator);
                tableRow.addView(winIndicator);
                if (!currentAttrValue.mAttribute.mLargerWins) {
                    winIndicator.setRotation(180);
                }

                /*Instantiating the TextViews with the correct style and adding
                them to the table row*/
                //TODO: find out why the hell those TextViews don`t accept all of the set styles
                TextView textViewAttrTitle = new TextView(tableRow.getContext(), null, 0,
                        R.style.TextViewCardAttributesTitle);
                textViewAttrTitle.setText(currentAttrValue.mAttribute.mName);
                tableRow.addView(textViewAttrTitle);

                TextView textViewAttrValue = new TextView(tableRow.getContext(),
                        null, 0, R.style.TextViewCardAttributesValue);
                textViewAttrValue.setText(currentAttrValue.mValue + " " +
                        "" + currentAttrValue.mAttribute.mUnit);
                tableRow.addView(textViewAttrValue);

                //finally adding the table row holding the attribute to the table
                tableLayoutAttributes.addView(tableRow);
            }
        }
    }
}
