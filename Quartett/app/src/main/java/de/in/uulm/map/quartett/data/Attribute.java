package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

/**
 * Created by alex on 12/17/16.
 */

public class Attribute extends SugarRecord {

    public String mName;
    public String mUnit;
    public boolean mLagerWins;

    public Attribute() {

    }

    public Attribute(String mName, String mUnit, boolean mLagerWins) {

        this.mName = mName;
        this.mUnit = mUnit;
        this.mLagerWins = mLagerWins;
    }
}
