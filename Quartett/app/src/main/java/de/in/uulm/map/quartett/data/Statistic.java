package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

/**
 * Created by alex on 12/17/16.
 */

public class Statistic extends SugarRecord {

    public String mTitle;
    public float mValue;
    public String mDescription;

    public Statistic() {

    }

    public Statistic(String title, float value, String description) {

        this.mTitle = title;
        this.mValue = value;
        this.mDescription = description;
    }
}

