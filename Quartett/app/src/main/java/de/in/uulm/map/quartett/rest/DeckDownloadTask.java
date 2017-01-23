package de.in.uulm.map.quartett.rest;

import android.os.AsyncTask;

import de.in.uulm.map.quartett.data.Deck;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by Jona on 23.01.2017.
 */

public class DeckDownloadTask extends AsyncTask<Void, Void, Void> {

    private final int mId;
    private final RestLoader mRestLoader;

    public interface Callback {

        void onFinished(boolean success);
    }

    public DeckDownloadTask(int id, RestLoader restLoader) {

        mId = id;
        mRestLoader = restLoader;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            RestLoader.Collector c = mRestLoader.loadDeck(mId);
            mRestLoader.loadImages(c.mImages);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
