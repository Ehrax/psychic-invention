package de.in.uulm.map.quartett.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;

/**
 * Created by maxka on 27.01.2017. Methods to handle some pixel and desitypixel
 * calculations and other measurement stuff.
 */

public class MetricsUtils {

    /**
     * Use this method to calculate the height of a given textView in pixels.
     * This is useful if you want to calculate the height of a textView before
     * it is actually created.
     *
     * @param context  context of the activity the textView will be created in.
     * @param textView a textView object created with the same style settings as
     *                 the textView which you want to know the height from.
     * @return the height of the given textView in pixels
     */
    public static int measureTextViewHeight(Context context, TextView textView) {

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(new DisplayMetrics().widthPixels,
                View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight() + dpToPx(context, 10);
    }

    /**
     * Use this method to convert dp into actual pixels.
     *
     * @param context context
     * @param dp      the amount of dp you want to convert to pixels
     * @return the given dp pixels
     */
    public static int dpToPx(Context context, int dp) {

        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
