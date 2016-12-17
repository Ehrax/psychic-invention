package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

/**
 * Created by alex on 12/17/16.
 */
public class Highscore extends SugarRecord {

    public int mValue;
    public int mPos;
    public String mName;
    public HighScoreType mType;

    public enum HighScoreType {

        ROUND, POINT, TIME, ONLINE;
    }

    public Highscore() {
    }

    public Highscore(int value, int pos, String name, HighScoreType type) {

        this.mValue = value;
        this.mPos = pos;
        this.mName = name;
        this.mType = type;
    }
}
