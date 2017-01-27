package de.in.uulm.map.quartett.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.util.MetricsUtils;

import java.util.List;

/**
 * Created by maxka on 23.01.2017. Simple adapter for the recycler view holding
 * the cards attributes.
 */

public class AttributeAdapter extends RecyclerView
        .Adapter<AttributeAdapter.ViewHolder> {

    private List<AttributeValue> mAttributeList;
    private int mHeight;

    private Context mContext;
    private CardFragment.AttributeClickListener mClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mRow;
        public ImageView mWinIndicator;
        public TextView mAttributeTitle;
        public TextView mAttributeValue;

        public ViewHolder(View v) {

            super(v);
            mRow = v;
            mWinIndicator = (ImageView) v.findViewById(R.id.img_win_indicator);
            mAttributeTitle = (TextView) v.findViewById(R.id.txt_attribute_title);
            mAttributeValue = (TextView) v.findViewById(R.id
                    .txt_attribute_value);


        }
    }

    public AttributeAdapter(List<AttributeValue> attributeList, Context ctx,
                            CardFragment.AttributeClickListener listener, int
                                    recyclerViewHeight) {

        mAttributeList = attributeList;
        mContext = ctx;
        mClickListener = listener;
        mHeight = recyclerViewHeight;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .card_attribute_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final AttributeValue currentAttributeValue = mAttributeList.get
                (position);

        if (!currentAttributeValue.mAttribute.mLargerWins) {
            viewHolder.mWinIndicator.setRotation(180);
        }
        if (currentAttributeValue.mAttribute.mName != null) {
            viewHolder.mAttributeTitle.setText(currentAttributeValue
                    .mAttribute.mName);
        }
        viewHolder.mAttributeValue.setText(currentAttributeValue.mValue + " " +
                "" + currentAttributeValue.mAttribute.mUnit);

        int padding = calculatePadding();

        if (padding > 0) {
            viewHolder.mAttributeTitle.setPadding(0, padding, 0, padding);
            viewHolder.mAttributeValue.setPadding(0, padding, 0, padding);
            viewHolder.mWinIndicator.setPadding(0, padding + MetricsUtils.dpToPx
                    (mContext, 2), 0, padding);
        }

        viewHolder.mRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mClickListener.OnItemClick(currentAttributeValue.mAttribute);
            }
        });

    }

    @Override
    public int getItemCount() {

        return mAttributeList.size();
    }

    /**
     * Calculating the padding for the attributes if they would not stretch over
     * the complete height of the card. Returns -1 if there is nothing to
     * change.
     *
     * @return -1 if there is nothing to change. Otherwise it returns the
     * correct padding to stretch the attributes over the complete card. (Use
     * this value for top and bottom padding.
     */
    private int calculatePadding() {

        TextView textView = new TextView(mContext, null, 0, R.style
                .TextViewCardAttributesTitle);

        int heightShouldBe = mHeight / (mAttributeList.size());
        int heightIs = MetricsUtils.measureTextViewHeight(mContext, textView);

        if (heightShouldBe < heightIs) {
            return -1;
        }

        return (heightShouldBe - heightIs) / 2;
    }


}
