package de.in.uulm.map.quartett.factory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by jona on 1/3/17.
 */

/**
 * This interface abstracts the process of loading a JSON file from an arbitrary
 * location. It also provides helpful methods for consistency purposes.
 */
public interface JsonLoader {

    int getHash();

    String getSource();

    JSONObject getJson() throws JSONException, IOException;
}
