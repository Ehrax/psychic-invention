package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by alex on 12/17/16.
 */

public class Deck extends SugarRecord {

    public String mTitle;
    public String mDescription;
    public Image mImage;
    public List<Card> mCards;
    public List<Attribute> mAttributes;

    public Deck() {

    }

    public Deck(String mTitle, String mDescription, Image mImage, List<Card>
            mCards, List<Attribute> mAttributes) {

        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mImage = mImage;
        this.mCards = mCards;
        this.mAttributes = mAttributes;
    }
}
