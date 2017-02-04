package de.in.uulm.map.quartett.rest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jona on 04.02.2017.
 */

/**
 * This class is used as a base class for all requests that obtain JSON data
 * from the server. These requests require authorization, which is implemented
 * in the getHeaders() method of this class.
 *
 * @param <T> the return type of the request
 */
public abstract class AuthRequest<T> extends Request<T> {

    /**
     * Server credentials. Cause hardcoding credentials has always been a
     * good idea ...
     */
    private static final String AUTH_STRING = "Basic YWRtaW46ZGIxJGFkbWlu";

    /**
     * Standard constructor, just calling super here.
     *
     * @param method the request methdo
     * @param url the target url
     * @param listener the error listener/callback
     */
    public AuthRequest(int method, String url, Response.ErrorListener listener) {

        super(method, url, listener);
    }

    /**
     * This will return the header field of the HTTP Request as a Map object.
     *
     * @return Map containing only the "Authorization" entry
     * @throws AuthFailureError
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        HashMap<String, String> map = new HashMap<>();
        map.put("Authorization", AUTH_STRING);

        return map;
    }
}
