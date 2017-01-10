package de.in.uulm.map.quartett.game;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;


import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.GameCard;
import de.in.uulm.map.quartett.data.Highscore;
import de.in.uulm.map.quartett.data.LocalGameState;
import de.in.uulm.map.quartett.gallery.CardFragment;
import de.in.uulm.map.quartett.util.AssetUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * Created by maxka on 08.01.2017. This Presenter holds the Core game logic.
 */

public class GamePresenter implements GameContract.Presenter {

    @NonNull
    private final GameContract.View mView;
    private final Context mCtx;
    /*
    holding all information about the current game state like ais and user
    deck and their points
     */
    private LocalGameState mCurrentGameState;
    /*
    interface to the activity to replace fragments
     */
    private GameActivity.CompareViewSwitcher mViewSwitcher;

    /*
    This CountDownLatch is needed to wait on UIThread until the
    AsyncDeckLoader loaded at least the first card.
     */
    CountDownLatch mCountDownLatchDeckLoader;
    /*
    This CountDownLatch is needed to wait on UIThread until the
    AsyncDeckRearranger saved the first card of the users deck.
     */
    CountDownLatch mCountDownLatchDeckRearranger;

    public GamePresenter(@NonNull GameContract.View gameView, Context ctx,
                         GameActivity.CompareViewSwitcher viewSwitcher) {

        mView = gameView;
        mCtx = ctx;
        mViewSwitcher = viewSwitcher;
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
                mCountDownLatchDeckLoader = new CountDownLatch(1);
                //loading the decks async into db
                new AsyncDeckLoader().execute(userAndAiDeck);
                try {
                    //wait until the first card of the user is loaded to
                    // continue showing the first card.
                    mCountDownLatchDeckLoader.await();
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
                currentDeck.size());

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
     * Attribute to be the one compared with the opponents one. It compares the
     * Attributes and determines who is the winner. This method increases the
     * winners points(+1). This method performs a fragment transaction. It
     * replaces the current Fragment with a GameCompareFragment!
     *
     * @param chosenAttr the attribute you want to compare
     */
    @Override
    public void chooseAttribute(Attribute chosenAttr) {

        GameCompareFragment compareFragment = GameCompareFragment.newInstance();
        //getting the users and ais value for the compared attribute
        float userAttributeValue = 0, aiAttributeValue = 0;
        List<AttributeValue> attributeValues = mCurrentGameState.getUserDeck
                ().get(0).mCard.getAttributeValues();
        for (AttributeValue attributeValue : attributeValues) {
            if (attributeValue.mAttribute.mName.equals(chosenAttr.mName)) {
                userAttributeValue = attributeValue.mValue;
            }
        }
        attributeValues = mCurrentGameState.getAIDeck().get(0).mCard
                .getAttributeValues();
        for (AttributeValue attributeValue : attributeValues) {
            if (attributeValue.mAttribute.mName.equals(chosenAttr.mName)) {
                aiAttributeValue = attributeValue.mValue;
            }
        }
        //check who won this round
        if (chosenAttr.mLargerWins) {
            if (userAttributeValue > aiAttributeValue) {
                mCurrentGameState.mUserPoints++;
                compareFragment.setRoundWinner(RoundWinner.USER);
            } else if (aiAttributeValue > userAttributeValue) {
                mCurrentGameState.mAIPoints++;
                compareFragment.setRoundWinner(RoundWinner.AI);
            } else {
                compareFragment.setRoundWinner(RoundWinner.DRAW);
            }
        } else {
            if (userAttributeValue > aiAttributeValue) {
                mCurrentGameState.mAIPoints++;
                compareFragment.setRoundWinner(RoundWinner.AI);

            } else if (aiAttributeValue > userAttributeValue) {
                mCurrentGameState.mUserPoints++;
                compareFragment.setRoundWinner(RoundWinner.USER);
            } else {
                compareFragment.setRoundWinner(RoundWinner.DRAW);
            }
        }
        compareFragment.setAttribute(chosenAttr);

        mCurrentGameState.save();
        mViewSwitcher.switchToView(compareFragment);

    }

    /**
     * Use this method to get information about the current game state
     *
     * @return the current game state of the local game.
     */
    @Override
    public LocalGameState getCurrentGameState() {

        return mCurrentGameState;
    }

    /**
     * Use this method to get the first Image of the Card which is currently on
     * top of the users or ais deck.
     *
     * @param fromUser set to true if you want the top card from the users deck
     *                 and false and if you want the top card of the ais deck
     */
    @Override
    public Drawable getCompareImage(boolean fromUser) {

        Drawable image;
        String imageUri;
        if (fromUser) {
            imageUri = mCurrentGameState.getUserDeck().get(0).mCard
                    .getCardImages().get(0).mImage.mUri;

        } else {
            imageUri = mCurrentGameState.getAIDeck().get(0).mCard
                    .getCardImages().get(0).mImage.mUri;
        }
        if (imageUri.contains("android_asset")) {
            image = AssetUtils.getDrawableFromAssetUri(mCtx, Uri
                    .parse(imageUri));
        } else {
            try {
                InputStream stream = mCtx.getContentResolver()
                        .openInputStream(Uri.parse(imageUri));
                image = Drawable.createFromStream(stream, imageUri);
            } catch (FileNotFoundException e) {
                image = mCtx.getResources().getDrawable(R.drawable
                        .ic_cards_playing);
            }
        }
        return image;
    }

    /**
     * Use this method to get the value of the given attribute from the top card
     * of the users or ais deck.
     *
     * @param fromUser  true if you want the value from the users card, false if
     *                  you want it from the ais card
     * @param attribute the attribute you want the value from
     * @return the value of the given attribute or -1 if the attribute is not
     * found
     */
    @Override
    public float getCompareAttributeValue(boolean fromUser, Attribute
            attribute) {

        List<AttributeValue> attributeValues;
        if (fromUser) {
            attributeValues = mCurrentGameState
                    .getUserDeck().get(0)
                    .mCard.getAttributeValues();
        } else {
            attributeValues = mCurrentGameState
                    .getAIDeck().get(0)
                    .mCard.getAttributeValues();
        }
        for (AttributeValue attributeValue : attributeValues) {
            if (attributeValue.mAttribute.mName.equals(attribute.mName)) {
                return attributeValue.mValue;
            }

        }
        return -1;
    }

    /**
     * This method is called when the user clicks on the compare fragment. It´s
     * checking if the round or point limit is reached. Decrementing the rounds
     * and rearrange the users and ais deck.This method performs a Fragment
     * Transaction.
     */
    @Override
    public void onClickCompare(RoundWinner winner) {
        //first of all rearrange the two decks.
        new AsyncDeckRearranger().execute(winner);
        //wait until the first card in the user deck is correctly saved
        // before continue on ui thread.
        mCountDownLatchDeckRearranger = new CountDownLatch(1);
        try {
            mCountDownLatchDeckRearranger.await();
        } catch (InterruptedException e) {
            //TODO: handle exception
        }

        if (mCurrentGameState.mGameMode == Highscore.HighScoreType.ROUND) {
            mCurrentGameState.mCurrentRound++;

        }
        if(mCurrentGameState.mIsUsersTurn){
            mCurrentGameState.mIsUsersTurn = false;
        }else{
            mCurrentGameState.mIsUsersTurn=true;
        }
        mCurrentGameState.save();
        GameFragment gameFragment = GameFragment.newInstance();
        mViewSwitcher.switchToView(gameFragment);
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
                new GameCard(card, mCurrentGameState, i, "user").save();
                if (i == 0) {
                    //first card loaded continue showing the first card.
                    mCountDownLatchDeckLoader.countDown();
                }
                i++;
            }
            i = 0;
            for (Card card : params[1]) {
                new GameCard(card, mCurrentGameState, i, "ai").save();
                i++;
            }
            return null;
        }

    }

    private class AsyncDeckRearranger extends AsyncTask<RoundWinner, Void, Void> {

        @Override
        protected Void doInBackground(RoundWinner... params) {
            //params[0] == RoundWinner winner
            if (params[0] == RoundWinner.USER) {
                //remove first card from ai deck and add it to user deck
                GameCard aiLostCard = mCurrentGameState.getAIDeck().get(0);
                aiLostCard.mOwner = "user";
                aiLostCard.mPositionInDeck = mCurrentGameState.getUserDeck().size();
                aiLostCard.save();
                //rearrange user deck
                List<GameCard> userDeck = mCurrentGameState.getUserDeck();
                userDeck.get(0).mPositionInDeck = userDeck.size();
                userDeck.get(0).save();
                for (GameCard gameCard : userDeck) {
                    gameCard.mPositionInDeck--;
                    gameCard.save();
                    //now it is save to load the first card from user deck on
                    // ui thread
                    if (gameCard.mPositionInDeck == 0) {
                        mCountDownLatchDeckRearranger.countDown();
                    }
                }
                //rearrange ais deck
                List<GameCard> aiDeck = mCurrentGameState.getAIDeck();
                for (GameCard gameCard : aiDeck) {
                    gameCard.mPositionInDeck--;
                    gameCard.save();
                }
                Log.d("DECKS: ", mCurrentGameState.getAIDeck().toString());
                Log.d("DECKS: ", mCurrentGameState.getUserDeck().toString());
            } else if (params[0] == RoundWinner.AI) {
                //removing the first card from the user deck and add it to
                // the ais deck
                GameCard userLostCard = mCurrentGameState.getUserDeck().get(0);
                userLostCard.mOwner = "ai";
                userLostCard.mPositionInDeck = mCurrentGameState.getAIDeck().size();
                userLostCard.save();
                //rearrange user deck
                List<GameCard> userDeck = mCurrentGameState.getUserDeck();
                for (GameCard gameCard : userDeck) {
                    gameCard.mPositionInDeck--;
                    gameCard.save();
                    //now it is save to load the first card on the ui thread
                    if (gameCard.mPositionInDeck == 0) {
                        mCountDownLatchDeckRearranger.countDown();
                    }
                }
                //rearrange ai deck
                List<GameCard> aiDeck = mCurrentGameState.getAIDeck();
                aiDeck.get(0).mPositionInDeck = aiDeck.size();
                aiDeck.get(0).save();
                for (GameCard gameCard : aiDeck) {
                    gameCard.mPositionInDeck--;
                    gameCard.save();
                }

                Log.d("DECKS: ", mCurrentGameState.getAIDeck().toString());
                Log.d("DECKS: ", mCurrentGameState.getUserDeck().toString());
            } else {
                //rearrange user deck
                List<GameCard> userDeck = mCurrentGameState.getUserDeck();
                userDeck.get(0).mPositionInDeck = userDeck.size();
                userDeck.get(0).save();
                for (GameCard gameCard : userDeck) {
                    gameCard.mPositionInDeck--;
                    gameCard.save();
                    //now it is save to load the first card from user deck on
                    // ui thread
                    if (gameCard.mPositionInDeck == 0) {
                        mCountDownLatchDeckRearranger.countDown();
                    }
                }
                //rearrange ai deck
                List<GameCard> aiDeck = mCurrentGameState.getAIDeck();
                aiDeck.get(0).mPositionInDeck = aiDeck.size();
                aiDeck.get(0).save();
                for (GameCard gameCard : aiDeck) {
                    gameCard.mPositionInDeck--;
                    gameCard.save();
                }
            }
            return null;
        }
    }
}
