package de.in.uulm.map.quartett.factory;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by jona on 1/3/17.
 */

public class AssetJsonLoader implements JsonLoader {

    /**
     * The current application context.
     */
    Context mContext;

    /**
     * The source string the Json will be loaded from.
     */
    String mSource;

    /**
     * The loaded json file.
     */
    JSONObject mJson;

    /**
     * The original json string mJson was build from.
     */
    String mJsonString;

    /**
     * Simple constructor to set the source this JsonLoader will load the Json
     * file from.
     *
     * @param mSource a valid path in the assets directory e.g.
     *                "decks/bikes/bikes.json"
     */
    public AssetJsonLoader(String mSource, Context mContext) {

        this.mSource = mSource;
        this.mContext = mContext;
    }

    /**
     * This function will load the JSON file given in mSource from the Assets
     * directory.
     *
     * @return a JSONObject representing the JSON file in mSource
     */
    @Override
    public JSONObject getJson() throws JSONException, IOException {

        if (mJson != null) {
            return mJson;
        }

        AssetManager am = mContext.getAssets();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(am.open(mSource)));

        StringWriter stringWriter = new StringWriter();

        char[] buffer = new char[1024];

        int n;
        while ((n = reader.read(buffer)) != -1) {
            stringWriter.write(buffer, 0, n);
        }

        mJsonString = stringWriter.toString();
        mJson = new JSONObject(mJsonString);

        return mJson;
    }

    /**
     * Returns the asset path for mSource. The path will be stored in
     * DeckInfo and can be used to check if the Deck has already been loaded
     * into the database.
     *
     * @return asset path for mSource
     */
    @Override
    public String getSource() {

        return "file:///android_asset/" + mSource;
    }

    /**
     * A hash of the JSON String loaded. This can be used the detect changes in
     * the JSON File.
     *
     * @return a 32bit hash value
     */
    @Override
    public int getHash() {

        return mJsonString.hashCode();
    }
}
