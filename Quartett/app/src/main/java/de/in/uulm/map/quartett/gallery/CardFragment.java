package de.in.uulm.map.quartett.gallery;

import android.graphics.drawable.Drawable;
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

    private LinearLayout mLinearLayoutCard;
    private ImageView mImageView;
    private TextView mTitleTextView;


    public static CardFragment newInstance() {

        return new CardFragment();
    }

    /**
     * Initialising the views and setting images and attributes.
     *
     * @param savedInstanceState standard bundle contains intent information
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

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

        for (int i = 0; i < mAttributeValues.size(); i++) {
            AttributeValue currentAttrValue = mAttributeValues.get(i);

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup
                    .LayoutParams.MATCH_PARENT, 0,(float)1/mAttributeValues.size
                    ()));

            TextView textViewAttrTitle = new TextView(getContext(),null,R
                    .style.TextViewCardAttributesTitle);
            textViewAttrTitle.setText(currentAttrValue.mAttribute.mName);
            linearLayout.addView(textViewAttrTitle);

            TextView textViewAttrValue = new TextView(getContext(),null,R
                    .style.TextViewCardAttributesValue);
            textViewAttrValue.setText(currentAttrValue.mValue+" " +
                    ""+currentAttrValue.mAttribute.mUnit);
            linearLayout.addView(textViewAttrValue);

            mLinearLayoutCard.addView(linearLayout);
        }

        //TODO: Implement multiple images and fade animation
        Uri cardImageUri = Uri.parse(mCardImages.get(0).mImage.mUri);
        if(!cardImageUri.getPath().contains("android_asset")) {
            mImageView.setImageURI(cardImageUri);
        }
        else{
            Drawable drawable = AssetUtils.getDrawableFromAssetUri(getContext
                    (),cardImageUri);
            mImageView.setImageDrawable(drawable);
        }

        mTitleTextView.setText(mCardTitle);

        return view;
    }

    public void setCardImageUris(List<CardImage> images) {

        mCardImages = images;
    }

    public void setCardAttributeValues(List<AttributeValue> values) {

        mAttributeValues = values;
    }

    public void setCardTitle(String cardTitle) {

        this.mCardTitle = cardTitle;
    }

}
