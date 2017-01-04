package de.in.uulm.map.quartett.data;

import android.net.Uri;

import com.orm.SugarRecord;
import com.orm.SugarTransactionHelper;

/**
 * Created by alex on 12/17/16.
 */

public class Image extends SugarRecord {

    public String mUri;
    public String mDescription;

    public Image() {

    }

    public Image(String mUri, String mDescription) {

        this.mUri = mUri;
        this.mDescription = mDescription;
    }

    /**
     * Use this method to delete an Image from the database. The image will not
     * be deleted if the image is still used in a Deck or in a CardImage.
     *
     * @return true
     */
    @Override
    public boolean delete() {

        SugarTransactionHelper.doInTransaction(new SugarTransactionHelper.Callback() {

            @Override
            public void manipulateInTransaction() {

                if (Deck.find(Deck.class,
                        "m_image = ?", "" + getId()).size() > 0) {
                    return;
                }

                if (CardImage.find(CardImage.class,
                        "m_image = ?", "" + getId()).size() > 0) {
                    return;
                }

                Image.super.delete();
            }
        });

        return true;
    }
}
