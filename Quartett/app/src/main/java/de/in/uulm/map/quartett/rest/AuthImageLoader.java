package de.in.uulm.map.quartett.rest;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Jona on 22.01.2017.
 */

public class AuthImageLoader extends ImageLoader {

    public AuthImageLoader(RequestQueue queue, ImageCache imageCache) {

        super(queue, imageCache);
    }

    @Override
    protected Request<Bitmap> makeImageRequest(String requestUrl,
                                               int maxWidth,
                                               int maxHeight,
                                               ImageView.ScaleType scaleType,
                                               final String cacheKey) {

        return new AuthImageRequest(
                requestUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        onGetImageSuccess(cacheKey, response);
                    }
                },
                maxWidth,
                maxHeight,
                scaleType,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        onGetImageError(cacheKey, error);
                    }
                });
    }
}
