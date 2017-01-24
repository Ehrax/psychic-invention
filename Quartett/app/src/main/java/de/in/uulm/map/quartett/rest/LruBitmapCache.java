package de.in.uulm.map.quartett.rest;

/**
 * Created by Jona on 22.01.2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * This is the example implementation of a Image Cache which is used by the
 * Volley Image Loader. Comments added. Code shamelessly ripped from here:
 *
 * https://developer.android.com/training/volley/request.html
 */
public class LruBitmapCache extends LruCache<String, Bitmap>
        implements ImageCache {

    /**
     * Use this constructor to create a cache with a fixed size.
     * @param maxSize
     */
    public LruBitmapCache(int maxSize) {

        super(maxSize);
    }

    /**
     * Use this constructor to create a cache with the cache size derived
     * from the current application context.
     * @param ctx
     */
    public LruBitmapCache(Context ctx) {

        this(getCacheSize(ctx));
    }

    /**
     * This method is used to get the size of a Bitmap.
     *
     * @param key not used
     * @param value the Bitmap to get the size from
     * @return the size of the Bitmap
     */
    @Override
    protected int sizeOf(String key, Bitmap value) {

        return value.getRowBytes() * value.getHeight();
    }

    /**
     * This function is used to get a Bitmap for a given url.
     *
     * @param url the url of the Bitmap
     * @return a Bitmap or null
     */
    @Override
    public Bitmap getBitmap(String url) {

        return get(url);
    }

    /**
     * This function is used to store a Bitmap in the Cache.
     *
     * @param url the url used as a key in the cache
     * @param bitmap the actual Bitmap
     */
    @Override
    public void putBitmap(String url, Bitmap bitmap) {

        put(url, bitmap);
    }

    /**
     * This will return the size of the cache.
     * The current size of the cache corresponds to about 3 x the size
     * of the device screen.
     *
     * @param ctx the context used to get the device screen size
     * @return the cache size in bytes
     */
    public static int getCacheSize(Context ctx) {

        final DisplayMetrics displayMetrics =
                ctx.getResources().getDisplayMetrics();

        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;
        final int screenBytes = screenWidth * screenHeight * 4;

        return screenBytes * 3;
    }
}
