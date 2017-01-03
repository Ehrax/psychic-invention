package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by alex on 12/17/16.
 */

public class Card extends SugarRecord {

    public String mTitle;
    public Deck mDeck;

    public Card() {

    }

    public Card(String mTitle, Deck mDeck) {

        this.mTitle = mTitle;
        this.mDeck = mDeck;
    }

    /**
     * Use this method to get a List of all CardImage objects of this Card.
     *
     * @return a List of CardImage objects
     */
    public List<CardImage> getCardImages() {

        return CardImage.find(CardImage.class, "m_card = ?", ""+this.getId());
    }

    /**
     * Use this method to get a List of all AttributeValue objects of this Card.
     *
     * @return a List of AttributeValue objects
     */
    public List<AttributeValue> getAttributeValues() {

        return AttributeValue.find(
                AttributeValue.class, "m_card = ?", ""+this.getId());
    }
}
