package de.in.uulm.map.quartett.game;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;


import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.GameCard;
import de.in.uulm.map.quartett.data.Highscore;
import de.in.uulm.map.quartett.data.LocalGameState;
import de.in.uulm.map.quartett.gallery.CardFragment;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.orm.SugarRecord;


/**
 * Created by maxka on 08.01.2017.
 */

public class GamePresenter implements GameContract.Presenter {

    @NonNull
    private final GameContract.View mView;
    private final Context mCtx;
    private LocalGameState mCurrentGameState;

    CountDownLatch mCountDownLatch;

    public GamePresenter(@NonNull GameContract.View gameView, Context ctx) {

        mView = gameView;
        mCtx = ctx;
    }

    /**
     * Use this method to create a CardFragment which contains the card on top
     * of the users deck.
     *
     * @return the card fragment containing te card on top of the users deck
     */
    @Override
    public CardFragment getCurrentCardFragment() {

        CardFragment currentCard = CardFragment.newInstance();
        currentCard.setCardImageUris(mCurrentGameState.getUserDeck().get(0).mCard
                .getCardImages(), mCtx);
        currentCard.setCardTitle(mCurrentGameState.getUserDeck().get(0)
                .mCard.mTitle);
        currentCard.setCardAttributeValues(mCurrentGameState.getUserDeck()
                .get(0).mCard.getAttributeValues());
        currentCard.setGamePresenter(this);
        return currentCard;
    }

    /**
     * Loading the game state if there is one. Otherwise create a new one.
     */
    @Override
    public void start() {

        try {
            LocalGameState localGameState = LocalGameState.findById
                    (LocalGameState.class, 1);
            if (localGameState != null) {
                mCurrentGameState = localGameState;
            } else {
                //TODO: get current deck from shared preferences
                List<Card>[] userAndAiDeck = shuffleDeck(1);
                mCurrentGameState = new LocalGameState(0, 0, 10, Highscore.HighScoreType
                        .ROUND);
                mCurrentGameState.save();
                mCountDownLatch = new CountDownLatch(1);
                //loading the decks async into db
                new AsyncDeckLoader().execute(userAndAiDeck);
                try {
                    //wait until the first card of the user is loaded to
                    // continue showing the first card.
                    mCountDownLatch.await();
                } catch (InterruptedException e) {
                    //TODO:handle exception
                }

            }
        } catch (SQLiteException e) {
            e.printStackTrace();

        }

    }

    /**
     * Use this method to shuffle a deck and split it in two decks.
     *
     * @param deckID the deckID of the deck you want to shuffle.
     * @return Two shuffled decks. One for each player.
     */
    private List<Card>[] shuffleDeck(long deckID) {

        List<Card> currentDeck = Deck.findById(Deck.class, deckID).getCards();
        Collections.shuffle(currentDeck);
        List<Card>[] userAndAiDeck = new List[2];
        userAndAiDeck[0] = currentDeck.subList(0, currentDeck.size() / 2);
        userAndAiDeck[1] = currentDeck.subList(currentDeck.size() / 2,
                currentDeck.size() - 1);

        return userAndAiDeck;
    }

    /**
     * Use this method to save the current game state
     */
    @Override
    public void saveGameState() {

        if (mCurrentGameState != null) {
            mCurrentGameState.save();
        }
    }

    /**
     * This method is called from the CardFragment class. It chooses the clicked
     * Attribute to be the one compared with the opponents one.
     *
     * @param chosenAttr the attribute you want to compare
     */
    @Override
    public void chooseAttribute(AttributeValue chosenAttr) {

        Toast.makeText(mCtx, chosenAttr.mAttribute.mName, Toast.LENGTH_LONG).show();
    }

    @Override
    public LocalGameState getCurrentGameState() {

        return mCurrentGameState;
    }

    /**
     * Loading the Users and AIs Decks into DB. This is a fucking long task
     * because we have to save them redundant because sugar orm can´t handle
     * neither list nor arrays.
     */
    private class AsyncDeckLoader extends AsyncTask<List<Card>, Void, Void> {

        @Override
        protected Void doInBackground(List<Card>... params) {

            int i = 0;
            for (Card card : params[0]) {
                new GameCard(card, mCurrentGameState, "user").save();
                if (i == 0) {
                    //first card loaded continue showing the first card.
                    mCountDownLatch.countDown();
                    i++;
                }

            }
            for (Card card : params[1]) {
                new GameCard(card, mCurrentGameState, "ai").save();
            }
            return null;
        }

    }
}
