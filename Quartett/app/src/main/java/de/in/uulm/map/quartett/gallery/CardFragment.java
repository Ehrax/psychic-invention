package de.in.uulm.map.quartett.gallery;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.CardImage;
import de.in.uulm.map.quartett.data.Image;
import de.in.uulm.map.quartett.util.AssetUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxka on 25.12.2016. Represents a single card. Used to show cards
 * in the deck detail view.
 */

public class CardFragment extends Fragment {

    private List<CardImage> mCardImages = new ArrayList<>();
    private List<AttributeValue> mAttributeValues = new ArrayList<>();
    private String mCardTitle;

    //root element of the fragment
    private LinearLayout mLinearLayoutCard;

    private ImageView mImageView;
    private TextView mTitleTextView;


    public static CardFragment newInstance() {

        return new CardFragment();
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

        mImageView = (ImageView) view.findViewById(R.id.img_card);
        mTitleTextView = (TextView) view.findViewById(R.id
                .txt_card_title);
        mLinearLayoutCard = (LinearLayout) view.findViewById(R.id
                .lin_layout_card);

        //TODO: Implement multiple images
        //Setting the Image, using AssetUtils if image is saved as asset!
        Uri cardImageUri = Uri.parse(mCardImages.get(0).mImage.mUri);
        if (!cardImageUri.getPath().contains("android_asset")) {
            mImageView.setImageURI(cardImageUri);
        } else {
            Drawable drawable = AssetUtils.getDrawableFromAssetUri(getContext
                    (), cardImageUri);
            mImageView.setImageDrawable(drawable);
        }
        //setting the title of the card
        mTitleTextView.setText(mCardTitle);

        //building the attribute layout
        for (int i = 0; i < mAttributeValues.size(); i++) {
            AttributeValue currentAttrValue = mAttributeValues.get(i);

            //this linear layout holds the attribute title aswell as the
            // attribute value
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup
                    .LayoutParams.MATCH_PARENT, 0, (float) 1 / mAttributeValues.size
                    ()));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setBackgroundColor(getResources().getColor(R.color
                    .colorCardAttributesBackground));

            //Instantiating the TextViews with the correct style and adding
            // them to the linear layout
            //TODO: find out why the hell those TextViews don`t accept all of the set styles
            TextView textViewAttrTitle = new TextView(linearLayout.getContext(), null, 0,
                    R.style.TextViewCardAttributesTitle);
            textViewAttrTitle.setText(currentAttrValue.mAttribute.mName);
            linearLayout.addView(textViewAttrTitle);

            TextView textViewAttrValue = new TextView(linearLayout.getContext(),
                    null, 0, R
                    .style.TextViewCardAttributesValue);
            textViewAttrValue.setText(currentAttrValue.mValue + " " +
                    "" + currentAttrValue.mAttribute.mUnit);
            linearLayout.addView(textViewAttrValue);

            //finally adding the linear layout holding the attribute to the
            // root element of the card fragment
            mLinearLayoutCard.addView(linearLayout);
        }

        return view;
    }

    /**
     * Use this method to set the cards images.
     *
     * @param images List of CardImages
     */
    public void setCardImageUris(List<CardImage> images) {

        mCardImages = images;
    }

    /**
     * Use this method to set the cards AttributeValues
     *
     * @param values List of AttributeValues
     */
    public void setCardAttributeValues(List<AttributeValue> values) {

        mAttributeValues = values;
    }

    /**
     * Use this method to set the cards title
     *
     * @param cardTitle String representing the cards title
     */
    public void setCardTitle(String cardTitle) {

        this.mCardTitle = cardTitle;
    }

}
