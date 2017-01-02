package de.in.uulm.map.quartett.factory;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.widget.Toast;

import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.DeckInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by jona on 1/2/17.
 */

/**
 * This class will attempt to import any not imported Decks from the assets
 * directory into the local database.
 */
public class EntityFactoryTask extends AsyncTask<Void, Void, Void> {

    /**
     * Keep the context for later.
     */
    private Context context;

    public EntityFactoryTask(Context context) {

        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            String[] strings = context.getAssets().list("decks");

            if(strings == null) {
                return null;
            }

            for (String s : strings) {

                String source = "file:///android_asset/decks/"+s+"/"+s+".json";
                JSONObject json = loadJson(source);

                List<DeckInfo> deckInfoList =
                        DeckInfo.find(DeckInfo.class, "m_source = ?", source);

                if(deckInfoList.size() < 1) {
                    continue;
                }

                EntityFactory entityFactory = new EntityFactory(json, source);
                entityFactory.save();


            }
        } catch (IOException | JSONException e) {
            Toast.makeText(context, "Decks konnten nicht geladen werden!",
                    Toast.LENGTH_LONG);
        }

        return null;
    }

    /**
     * Use this method to load a JSON file from somewhere in the assets
     * directory.
     *
     * @param path the path to load the JSON file from.
     * @return
     * @throws IOException
     */
    private JSONObject loadJson(String path) throws IOException, JSONException {

        AssetManager am = context.getAssets();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(am.open(path)));

        StringWriter stringWriter = new StringWriter();

        char[] buffer = new char[1024];

        int n;
        while ((n = reader.read(buffer)) != -1) {
            stringWriter.write(buffer, 0, n);
        }

        return new JSONObject(stringWriter.toString());
    }
}
