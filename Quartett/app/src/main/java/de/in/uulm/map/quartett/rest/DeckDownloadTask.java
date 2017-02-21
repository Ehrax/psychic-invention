package de.in.uulm.map.quartett.rest;

import android.os.AsyncTask;

import com.orm.SugarRecord;
import com.orm.SugarTransactionHelper;

import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.DeckInfo;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by Jona on 23.01.2017.
 */

public class DeckDownloadTask extends AsyncTask<Void, Deck, Deck> {

    private final int mId;
    private final RestLoader mRestLoader;
    private final Callback mCallback;

    public DeckDownloadTask(int id,
                            RestLoader restLoader,
                            Callback callback) {

        mId = id;
        mRestLoader = restLoader;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(Deck result) {

        if(!isCancelled()) {
            mCallback.onFinished(result);
        }
        super.onPostExecute(result);
    }

    @Override
    protected Deck doInBackground(Void... voids) {

        try {
            final RestLoader.Collector c = mRestLoader.loadDeck(mId);
            mRestLoader.loadImages(c.mImages);

            SugarTransactionHelper.doInTransaction(new SugarTransactionHelper.Callback() {

                @Override
                public void manipulateInTransaction() {

                    if(DeckInfo.find(DeckInfo.class, "m_source = ?",
                            c.mDeckInfo.mSource).size() > 0) {
                        return;
                    }

                    SugarRecord.saveInTx(c.mImages);
                    c.mDeckInfo.save();
                    c.mDeck.save();
                    SugarRecord.saveInTx(c.mAttributes);
                    SugarRecord.saveInTx(c.mCards);
                    SugarRecord.saveInTx(c.mAttributeValues);
                    SugarRecord.saveInTx(c.mCardImages);
                }
            });

            return c.mDeck;

        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface Callback {

        void onFinished(Deck deck);
    }
}
