package de.in.uulm.map.quartett.gallery;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maxka on 25.12.2016. Represents a single card. Used to show cards
 * in the deck detail view.
 */

public class CardFragment extends Fragment {

    private List<Image> mCardImages = new ArrayList<>();
    private List<Attribute> mCardAttributes = new ArrayList<>();
    private List<AttributeValue> mAttributeValues = new ArrayList<>();
    private String mCardTitle;

    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView[] mAttrTitleTextViews = new TextView[4];
    private TextView[] mAttrValueTextViews = new TextView[4];


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

        /*mImageView = (ImageView) getActivity().findViewById(R.id.img_card);
        mTitleTextView = (TextView) getActivity().findViewById(R.id
                .txt_card_title);
        for (int i = 0; i < mAttrTitleTextViews.length; i++) {
            String textViewTitleID = "txt_attr_title_" + i;
            String textViewValueID = "txt_attr_value_" + i;

            int resIDTitle = getResources().getIdentifier(textViewTitleID, "id",
                    getActivity().getPackageName());
            int resIDValue = getResources().getIdentifier(textViewValueID,
                    "id", getActivity().getPackageName());

            mAttrTitleTextViews[i] = (TextView) getActivity().findViewById(resIDTitle);
            mAttrValueTextViews[i] = (TextView) getActivity().findViewById(resIDValue);

            mAttrTitleTextViews[i].setText(mAttributeValues.get(i).mAttribute.mName);
            mAttrValueTextViews[i].setText(mAttributeValues.get(i).mValue + " " + mAttributeValues.get(i).mAttribute.mUnit);
        }

        //TODO: Implement multiple images and fade animation
        mImageView.setImageURI(mCardImages.get(0).mUri);
        mTitleTextView.setText(mCardTitle);
        */

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_card, container, false);
    }

    public void setCardImageUris(List<Image> images) {

        mCardImages = images;
    }

    public void setCardAttributes(List<Attribute> attr) {

        mCardAttributes = attr;
    }

    public void setCardAttributeValues(List<AttributeValue> values) {

        mAttributeValues = values;
    }

    public void setCardTitle(String cardTitle) {

        this.mCardTitle = cardTitle;
    }

}
