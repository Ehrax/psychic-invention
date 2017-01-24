package de.in.uulm.map.quartett.rest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Jona on 22.01.2017.
 */

/**
 * This class is necessary to allow Authorization via HTTP.
 */
public class AuthJsonObjectRequest extends JsonObjectRequest {

    public AuthJsonObjectRequest(int method,
                                 String url,
                                 JSONObject jsonRequest,
                                 Response.Listener<JSONObject> listener,
                                 Response.ErrorListener errorListener) {

        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        return RestLoader.getAuthHeader();
    }
}
