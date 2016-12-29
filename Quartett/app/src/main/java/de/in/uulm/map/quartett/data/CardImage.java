package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

/**
 * Created by jona on 12/29/16.
 */

public class CardImage extends SugarRecord {

    public Card mCard;
    public Image mImage;

    public CardImage() {

    }

    public CardImage(Card mCard, Image mImage) {

        this.mCard = mCard;
        this.mImage = mImage;
    }
}
