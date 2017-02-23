package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by jona on 1/2/17.
 */

public class DeckInfo extends SugarRecord {

    public String mSource;
    public int mHash;
    public long mTimestamp;
    public int mProgress;

    public DeckInfo() {

    }

    public DeckInfo(String mSource, int mHash, long mTimestamp, int mProgress) {

        this.mSource = mSource;
        this.mHash = mHash;
        this.mTimestamp = mTimestamp;
        this.mProgress = mProgress;
    }

    public Deck getDeck() {

        List<Deck> decks =
                Deck.find(Deck.class, "m_deckinfo = ?", "" + this.getId());

        return decks.size() > 0 ? decks.get(0) : null;
    }
}
