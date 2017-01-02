package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

import java.sql.Date;

/**
 * Created by jona on 1/2/17.
 */

public class DeckInfo extends SugarRecord {

    public Deck mDeck;
    public String mSource;
    public int mHash;
    public Date mDate;

    public DeckInfo(Deck mDeck, String mSource, int mHash, Date mDate) {

        this.mDeck = mDeck;
        this.mSource = mSource;
        this.mHash = mHash;
        this.mDate = mDate;
    }
}
