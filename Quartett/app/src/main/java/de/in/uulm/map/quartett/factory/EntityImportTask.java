package de.in.uulm.map.quartett.factory;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import de.in.uulm.map.quartett.data.DeckInfo;
import de.in.uulm.map.quartett.data.Image;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jona on 1/2/17.
 */

/**
 * This class will attempt to import any not imported Decks from the assets
 * directory into the local database. Simply run it anywhere and all decks in
 * the assets directory will be imported.
 */
public class EntityImportTask extends AsyncTask<Void, Void, Integer> {

    /**
     * Keep the mContext for the EntityFactory.
     */
    private Context mContext;

    /**
     * This will be called when the Task is finished.
     */
    private Callback mCallback;

    /**
     * Simple constructor to initialize member variables.
     *
     * @param mContext  the current application mContext
     * @param mCallback a function to be called when the task has finished
     */
    public EntityImportTask(Context mContext, @Nullable Callback mCallback) {

        this.mContext = mContext;
        this.mCallback = mCallback;
    }

    /**
     * This method will search through the decks directory in the the assets
     * folder and will attempt to load a deck for each found folder.
     *
     * @param voids just pass nothing, parameters are not used
     * @return null
     */
    @Override
    protected Integer doInBackground(Void... voids) {

        final String[] strings;

        try {
            strings = mContext.getAssets().list("decks");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        CountDownLatch saveLatch = new CountDownLatch(strings.length);

        for (String s : strings) {

            if (isCancelled()) {
                break;
            }

            if (isImported(s)) {
                saveLatch.countDown();
                continue;
            }

            try {
                importDeck(s, saveLatch);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                saveLatch.countDown();
            }
        }

        try {
            saveLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(mCallback != null) {
            mCallback.onImportFinished();
        }

        return strings.length;
    }

    /**
     * This function is used to determine if a Deck has already been imported.
     *
     * @param deckFolder the name of the Deck folder
     * @return true: deck is imported, false: deck is not imported
     */
    private boolean isImported(String deckFolder) {

        return DeckInfo.find(DeckInfo.class, "m_source = ?",
                "file:///android_asset/decks/" + deckFolder + "/" + deckFolder +
                        ".json").size() > 0;
    }

    /**
     * Use this method to import a deck into the database. The deck will only be
     * imported if the it does not already exist in the database.
     *
     * @param deckFolder the name of the Deck folder
     */
    private void importDeck(String deckFolder, final CountDownLatch saveLatch)
            throws IOException, JSONException {

        AssetJsonLoader jsonLoader = new AssetJsonLoader(
                "decks/" + deckFolder + "/" + deckFolder + ".json", mContext);

        EntityFactory entityFactory = new EntityFactory(jsonLoader);
        entityFactory.loadDeck();

        String path = new File(jsonLoader.getSource()).getParent();

        for (Image i : entityFactory.getImages()) {
            i.mUri = path + "/" + i.mUri;
        }

        entityFactory.save(new EntityFactory.Callback() {

            @Override
            public void onSaved() {

                saveLatch.countDown();
            }
        });
    }

    /**
     * Use this interface to get notified when all Decks are fully imported.
     */
    public interface Callback {

        void onImportFinished();
    }
}
