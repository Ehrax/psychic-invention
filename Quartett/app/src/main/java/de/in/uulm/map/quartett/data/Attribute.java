package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

/**
 * Created by alex on 12/17/16.
 */

public class Attribute extends SugarRecord {

    public String mName;
    public String mUnit;
    public boolean mLargerWins;
    public Deck mDeck;

    public Attribute() {

    }

    public Attribute(String mName, String mUnit, boolean mLargerWins,
                     Deck mDeck) {

        this.mName = mName;
        this.mUnit = mUnit;
        this.mLargerWins = mLargerWins;
        this.mDeck = mDeck;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof Attribute) {
            Attribute a = (Attribute) o;
            return mName.equals(a.mName) && mDeck.equals(a.mDeck);
        }

        return false;
    }

}
