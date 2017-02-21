package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;
import com.orm.SugarTransactionHelper;

import java.util.List;

/**
 * Created by alex on 12/17/16.
 */

public class Card extends SugarRecord {

    public String mTitle;
    public Deck mDeck;
    public int mPosition;
    public int mServerId;

    public Card() {

    }

    public Card(String mTitle, Deck mDeck,int mPosition) {

        this.mTitle = mTitle;
        this.mDeck = mDeck;
        this.mPosition = mPosition;
    }

    /**
     * Use this method to delete a Card an all associated AttributeValues and
     * CardImages that are not used by another card.
     *
     * @return true
     */
    @Override
    public boolean delete() {

        SugarTransactionHelper.doInTransaction(new SugarTransactionHelper.Callback() {
            @Override
            public void manipulateInTransaction() {

                Card.super.delete();

                for (CardImage c : getCardImages()) {
                    c.delete();
                }

                for (AttributeValue a : getAttributeValues()) {
                    a.delete();
                }
            }
        });

        return true;
    }

    /**
     * Use this method to get a List of all CardImage objects of this Card.
     *
     * @return a List of CardImage objects
     */
    public List<CardImage> getCardImages() {

        return CardImage.find(CardImage.class, "m_card = ?", "" + this.getId());
    }

    /**
     * Use this method to get a List of all AttributeValue objects of this
     * Card.
     *
     * @return a List of AttributeValue objects
     */
    public List<AttributeValue> getAttributeValues() {

        return AttributeValue.find(
                AttributeValue.class, "m_card = ?", "" + this.getId());
    }
}
