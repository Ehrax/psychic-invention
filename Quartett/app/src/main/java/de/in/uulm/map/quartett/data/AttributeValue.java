package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

/**
 * Created by alex on 12/17/16.
 */

public class AttributeValue extends SugarRecord {

    public float mValue;
    public Attribute mAttribute;
    public Card mCard;

    public AttributeValue() {

    }

    public AttributeValue(float mValue, Attribute mAttribute, Card mCard) {

        this.mValue = mValue;
        this.mAttribute = mAttribute;
        this.mCard = mCard;
    }
}
