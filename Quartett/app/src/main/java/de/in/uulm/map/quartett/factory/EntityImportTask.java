package de.in.uulm.map.quartett.factory;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import de.in.uulm.map.quartett.data.DeckInfo;
import de.in.uulm.map.quartett.data.Image;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by jona on 1/2/17.
 */

/**
 * This class will attempt to import any not imported Decks from the assets
 * directory into the local database.
 */
public class EntityImportTask extends AsyncTask<Void, Void, Void> {

    /**
     * Keep the context for the EntityFactory.
     */
    private Context context;

    /**
     * Simple constructor to initialize member variables.
     *
     * @param context the current application context
     */
    public EntityImportTask(Context context) {

        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            String[] strings = context.getAssets().list("decks");

            if (strings == null) {
                return null;
            }

            for (String s : strings) {
                if (!this.isCancelled()) {
                    importDeck(s);
                }
            }
        } catch (IOException | JSONException e) {
            /*Toast.makeText(context, "Decks konnten nicht geladen werden!",
                    Toast.LENGTH_LONG);*/
            e.printStackTrace();
        }

        return null;
    }

    private void importDeck(String deckName)
            throws IOException, JSONException {

        String source = "decks/" + deckName + "/" + deckName + ".json";

        AssetJsonLoader jsonLoader =
                new AssetJsonLoader(source, context);

        List<DeckInfo> deckInfoList =
                DeckInfo.find(DeckInfo.class, "m_source = ?",
                        jsonLoader.getSource());

        List<DeckInfo> deckInfos = DeckInfo.listAll(DeckInfo.class);

        if (deckInfoList.size() > 0) {
            return;
        }

        EntityFactory entityFactory = new EntityFactory(jsonLoader);
        entityFactory.loadDeck();

        String path = new File(jsonLoader.getSource()).getParent();

        for (Image i : entityFactory.getImages()) {
            i.mUri = path + "/" + i.mUri;
        }

        entityFactory.save();
    }
}
