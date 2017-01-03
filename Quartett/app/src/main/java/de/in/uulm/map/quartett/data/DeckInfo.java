package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

/**
 * Created by jona on 1/2/17.
 */

public class DeckInfo extends SugarRecord {

    public Deck mDeck;
    public String mSource;
    public int mHash;
    public long mTimestamp;

    public DeckInfo() {

    }

    public DeckInfo(Deck mDeck, String mSource, int mHash, long mTimestamp) {

        this.mDeck = mDeck;
        this.mSource = mSource;
        this.mHash = mHash;
        this.mTimestamp = mTimestamp;
    }
}
