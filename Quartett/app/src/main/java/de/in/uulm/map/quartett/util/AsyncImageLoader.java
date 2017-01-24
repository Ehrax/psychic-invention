package de.in.uulm.map.quartett.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.rest.RestLoader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Jona on 28.11.2016.
 */

/**
 * This class should be used for all ImageView that load something other than a
 * vector drawable or really small images.
 */
public class AsyncImageLoader extends AsyncTask<Void, Void, Bitmap> {

    final private String mUri;

    final private WeakReference<ImageView> mView;

    final private Context mContext;

    /**
     * This constructor will set all required member variables. If the uri
     * can contain web urls a RestLoader must be set. Else the RestLoader is
     * not required and can be set to null.
     *
     * @param uri the uri of the image
     * @param view the view in which the image should be loaded
     * @param context the current context
     * @param loader a rest loader or null
     */
    public AsyncImageLoader(String uri,
                            ImageView view,
                            Context context,
                            @Nullable RestLoader loader) {

        this.mView = new WeakReference<>(view);
        this.mContext = context;

        if (uri.contains("http://") && loader != null) {
            loader.loadImage(uri, view, R.drawable.empty, R.drawable.empty);
            this.mUri = null;
        } else {
            view.setTag(uri);
            this.mUri = uri;
        }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        if (mUri == null || mUri.isEmpty()) {
            return null;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bitmap;

        try (InputStream in = getInputStream(mUri)) {
            BitmapFactory.decodeStream(in, null, options);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        options.inSampleSize = calculateInSampleSize(options, 512, 512);
        options.inJustDecodeBounds = false;

        if (isCancelled()) {
            return null;
        }

        try (InputStream in = getInputStream(mUri)) {
            bitmap = BitmapFactory.decodeStream(in, null, options);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (this.isCancelled()) {
            return;
        }

        if (bitmap != null && mView != null) {
            final ImageView img_view = mView.get();
            if (img_view != null && img_view.getTag().equals(mUri)) {
                img_view.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * Shamelessly ripped from here:
     *
     * https://developer.android.com/training/displaying-bitmaps/load-bitmap.html#load-bitmap
     */
    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * This function is used to open the right input stream for a uri.
     *
     * @param mUri a valid uri to the image
     * @return an opened input stream
     */
    private InputStream getInputStream(String mUri) throws IOException {

        if (mUri.contains("android_asset")) {
            return mContext.getAssets().open(mUri.substring(20));
        }

        return mContext.openFileInput(mUri);
    }
}
