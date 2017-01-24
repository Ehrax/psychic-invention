package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

/**
 * Created by alex on 12/17/16.
 */
public class Highscore extends SugarRecord {

    public int mValue;
    public String mName;


    public Highscore() {

    }

    public Highscore(int value, String name) {

        this.mValue = value;
        this.mName = name;

    }
}
