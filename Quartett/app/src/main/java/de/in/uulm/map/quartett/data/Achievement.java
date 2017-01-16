package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

/**
 * Created by alex on 12/17/16.
 */

public class Achievement extends SugarRecord {

    public String mTitle;
    public float mValue;
    public float mTargetValue;
    public String mDescription;

    public Achievement() {

    }

    public Achievement(String mTitle, float mValue, float mTargetValue) {

        this.mTitle = mTitle;
        this.mValue = mValue;
        this.mTargetValue = mTargetValue;
    }
}

