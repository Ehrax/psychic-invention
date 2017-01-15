package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

/**
 * Created by maxka on 08.01.2017.
 * This class represents a card in a deck of the current local game.
 * I´ve chosen this way because sugar can´t handle lists and arrays.
 * It is redundant and the performance sucks xD
 * If someone has a better idea pls come up with it ;)
 * This was driving me crazy.
 */

public class GameCard extends SugarRecord {
    public LocalGameState mGameState;
    public Card mCard;
    public String mOwner;
    public int mPositionInDeck;

    public GameCard(){

    }

    public GameCard(Card card, LocalGameState gameState,int position,String
                    owner){
        mGameState=gameState;
        mCard = card;
        mOwner=owner;
        mPositionInDeck=position;

    }

    @Override
    public String toString() {

        return "ID: "+getId()+" Position: "+mPositionInDeck;
    }
}
