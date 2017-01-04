package de.in.uulm.map.quartett.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import de.in.uulm.map.quartett.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by maxka on 04.01.2017. This class offers helper methods to hadle
 * some asset actions.
 */

public class AssetUtils {

    /**
     * Use this method to get a drawable from a given Uri of an asset. The asset
     * must be a image otherwise this method will return null.
     *
     * @param context  the context where this method runs in. It is needed for
     *                 the AssetManager
     * @param assetUri the Uri of the Image you want to make a drawable from.
     * @return a drawable containing the image which is saved at the given Uri
     */
    public static Drawable getDrawableFromAssetUri(Context context, Uri
            assetUri) {

        AssetManager am = context.getAssets();
        Drawable drawable;

        try {
            /*cut off the first 15 characters because Uri looks like:
             /android_asset/path_here....
             */
            InputStream inputStream = am.open(assetUri
                    .getPath().substring(15));
            drawable = Drawable.createFromStream
                    (inputStream, null);
            inputStream.close();

        } catch (IOException ioe) {
            return null;
        }
        return drawable;
    }

}
