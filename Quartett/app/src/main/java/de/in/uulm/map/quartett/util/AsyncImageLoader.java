package de.in.uulm.map.quartett.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.rest.Network;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Jona on 28.11.2016.
 */

/**
 * This class should be used for all ImageView that load something other than
 * a vector drawable or really small images.
 */
public class AsyncImageLoader extends AsyncTask<Void, Void, Bitmap> {

    final private String mUri;

    final private WeakReference<ImageView> mView;

    final private Context mContext;

    public AsyncImageLoader(String uri,
                            WeakReference<ImageView> view,
                            Context context) {

        this.mUri = uri;
        this.mView = view;
        this.mContext = context;

        if(view.get().getTag() != null && view.get().getTag().equals(mUri)) {
            return;
        }

        if(mUri.contains("http://")) {

            Network.getInstance(mContext)
                    .getImageLoader()
                    .get(mUri, ImageLoader.getImageListener(
                            mView.get(), R.drawable.empty, R.drawable.empty));
        }

        view.get().setTag(uri);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        if(mUri.contains("http://") || mUri.isEmpty()) {
            return null;
        }

        return loadBitmap(mUri);
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
     * This function is used to open the right input stream for any uri.
     *
     * @param mUri a valid uri to the image
     * @return an opened input stream
     */
    private InputStream getInputStream(String mUri) throws IOException {

        if(mUri.contains("android_asset")) {
            return mContext.getAssets().open(mUri.substring(20));
        }

        return mContext.openFileInput(mUri);
    }

    /**
     * Use this to load a very low sampled version of an image.
     *
     * @return the loaded bitmap
     */
    private Bitmap loadBitmap(String uri) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bitmap;

        try (InputStream in = getInputStream(uri)) {
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

        try (InputStream in = getInputStream(uri)) {
            bitmap = BitmapFactory.decodeStream(in, null, options);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return bitmap;
    }
}
