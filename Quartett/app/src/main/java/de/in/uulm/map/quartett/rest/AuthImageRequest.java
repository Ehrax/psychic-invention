package de.in.uulm.map.quartett.rest;

import android.graphics.Bitmap;
import android.media.ImageReader;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

import java.util.Map;

/**
 * Created by Jona on 22.01.2017.
 */

/**
 * This class is necessary to allow Authorization via HTTP.
 */
public class AuthImageRequest extends ImageRequest {

    public AuthImageRequest(String url,
                            Response.Listener<Bitmap> listener,
                            int maxWidth,
                            int maxHeight,
                            ImageView.ScaleType scaleType,
                            Bitmap.Config decodeConfig,
                            Response.ErrorListener errorListener) {

        super(url, listener, maxWidth, maxHeight, scaleType,
                decodeConfig, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        return RestLoader.getAuthHeader();
    }
}
