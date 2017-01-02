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

    public Deck() {

    }

    public Deck(String mTitle, String mDescription, Image mImage) {

        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mImage = mImage;
    }

    /**
     * Use this method to get a List of all Card objects of this Deck.
     *
     * @return a List of Card objects.
     */
    public List<Card> getCards() {

        return Card.find(Card.class, "m_deck = ?", "" + this.getId());
    }

    /**
     * Use this method to get a List of all Attribute objects of this Deck.
     *
     * @return a List of Attribute objects
     */
    public List<Attribute> getAttributes() {

        return Card.find(Attribute.class, "m_deck = ?", "" + this.getId());
    }
}
