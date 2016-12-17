package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by alex on 12/17/16.
 */

public class Card extends SugarRecord {
    public String mTitle;
    public List<Image> mImages;
    public List<AttributeValue> mAttributeValues;

    public Card() {
    }

    public Card(String mTitle, List<Image> mImages, List<AttributeValue>
            mAttributeValues) {
        this.mTitle = mTitle;
        this.mImages = mImages;
        this.mAttributeValues = mAttributeValues;
    }
}
