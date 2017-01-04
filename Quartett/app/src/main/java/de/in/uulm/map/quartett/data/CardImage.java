package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;
import com.orm.SugarTransactionHelper;

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

    /**
     * Use this method to delete the CardImage and the associated Image. The
     * Image will only be delete if it is not used by another card image.
     */
    @Override
    public boolean delete() {

        SugarTransactionHelper.doInTransaction(new SugarTransactionHelper.Callback() {
            @Override
            public void manipulateInTransaction() {

                CardImage.super.delete();
                mImage.delete();
            }
        });

        return true;
    }
}
