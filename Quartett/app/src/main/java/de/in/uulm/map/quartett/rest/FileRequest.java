package de.in.uulm.map.quartett.rest;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jona on 22.01.2017.
 */
public class FileRequest extends Request<String> {

    final private Context mContext;

    final private Response.Listener<String> mListener;

    final private String mFilePath;

    /**
     * This calls the super constructor and initializes member variables.
     *
     * @param url the url from which to download
     * @param filePath the path in the internal storage
     * @param listener callback when file is stored
     * @param errorListener callback for errors
     * @param context the application context
     */
    public FileRequest(String url,
                       String filePath,
                       Response.Listener<String> listener,
                       Response.ErrorListener errorListener,
                       Context context) {

        super(Method.GET, url, errorListener);

        mListener = listener;
        mContext = context;
        mFilePath = filePath;
    }

    /**
     * This will be called by Volley when the network got a response.
     * The response will be written to disk at the given url.
     *
     * @param response the network response
     * @return a Response object containing the file path
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        if (response.statusCode != 200) {
            return Response.error(new VolleyError(response));
        }

        try (FileOutputStream out =
                     mContext.openFileOutput(mFilePath, Context.MODE_PRIVATE)) {

            out.write(response.data);
            out.flush();

        } catch (IOException e) {
            return Response.error(new VolleyError(e));
        }

        return Response.success(mFilePath,
                HttpHeaderParser.parseCacheHeaders(response));
    }

    /**
     * This function will be called on the main thread. It calls the listener.
     * Will only be called if parseNetworkResponse has finished successfully.
     *
     * @param response the response generated in parseNetworkResponse
     */
    @Override
    protected void deliverResponse(String response) {

        mListener.onResponse(response);
    }
}
