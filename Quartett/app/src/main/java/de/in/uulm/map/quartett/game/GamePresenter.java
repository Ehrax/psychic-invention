package de.in.uulm.map.quartett.game;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;


import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.GameCard;
import de.in.uulm.map.quartett.data.LocalGameState;
import de.in.uulm.map.quartett.gallery.CardFragment;
import de.in.uulm.map.quartett.gameend.GameEndActivity;
import de.in.uulm.map.quartett.gameend.GameEndPresenter;
import de.in.uulm.map.quartett.gameend.GameEndState;
import de.in.uulm.map.quartett.gamesettings.GameMode;
import de.in.uulm.map.quartett.gamesettings.GameSettingsPresenter;
import de.in.uulm.map.quartett.util.AssetUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;


/**
 * Created by maxka on 08.01.2017. This Presenter holds the Core game logic.
 */

public class GamePresenter implements GameContract.Presenter {

    @NonNull
    private GameContract.View mView;
    private final Context mCtx;
    /*
    holding all information about the current game state like ais and user
    deck and their points
     */
    private LocalGameState mCurrentGameState;
    /*
    If the user wants to start a new game (true) or continue the existing one
     */
    private boolean mIsStartingNewGame;
    /*
    This bundle is given by the GameSettingsActivity and is only needed once
    to set the mCurrentGameState
     */
    private Bundle mGameSettings;
    /*
    interface to the activity to replace fragments
     */
    private GameContract.BackEnd mBackEnd;

    /*
    This CountDownLatch is needed to wait on UIThread until the
    AsyncDeckLoader loaded at least the first card.
     */
    private CountDownLatch mCountDownLatchDeckLoader;
    /*
    The "AI" AsyncTask
     */
    AI mAI = new AI();
    /*
    The users and ais image of the currently compared cards. And Values of
    the compared attributes. This is saved for the compare view so we can
    rearrange the decks in the background.
     */
    private Drawable mUserCompareImage;
    private Drawable mAICompareImage;
    private float mUserCompareValue;
    private float mAICompareValue;

    private AsyncDeckRearranger mDeckRearranger;

    public static GameTimer GameTimer;

    public GamePresenter(@NonNull GameContract.View gameView, Bundle
            gameSettings, Context ctx,
                         GameContract.BackEnd viewSwitcher) {

        mView = gameView;
        mCtx = ctx;
        mBackEnd = viewSwitcher;
        mGameSettings = gameSettings;
    }

    @Override
    public void setView(GameContract.View view) {

        mView = view;
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
     * Loading the game state if there is one. Otherwise create a new one. If
     * starting a new game and there is a local game state delete it and create
     * a new one.
     */
    @Override
    public void start() {

        try {
            List<LocalGameState> localGameState = LocalGameState.listAll
                    (LocalGameState.class);
            if (!localGameState.isEmpty()) {
                if (!mIsStartingNewGame) {
                    mCurrentGameState = localGameState.get(0);
                    if (mCurrentGameState.mGameMode == GameMode.TIME) {
                        GameTimer = new GameTimer(mCurrentGameState
                                .mGameTimeInMillis - mCurrentGameState
                                .mCurrentTimeInMillis, 1000);
                        GameTimer.start();
                    }
                } else {
                    localGameState.get(0).delete();
                    createNewGame();
                }
            } else {
                createNewGame();
            }
            mIsStartingNewGame = false;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Use this method to set a new GameState. This method starts an AsyncTask
     * to load the users and ais deck and create the GameCard objects for it.
     * This method may cause an interrupted exception which is currently not
     * handled xD
     */
    private void createNewGame() {
        //TODO: get current deck from shared preferences or intent

        mCurrentGameState = new LocalGameState(mGameSettings.getLong
                (GameSettingsPresenter.TIME), mGameSettings.getInt
                (GameSettingsPresenter.POINTS), mGameSettings.getInt
                (GameSettingsPresenter.ROUNDS), (GameMode) mGameSettings
                .getSerializable(GameSettingsPresenter.MODE), mGameSettings.getString
                (GameSettingsPresenter.NAME));
        mCurrentGameState.save();
        GameCard.deleteAll(GameCard.class);
        List<Card>[] userAndAiDeck = shuffleDeck(1);
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

        if (GameTimer != null) {
            GameTimer.cancel();
        }
        if (mCurrentGameState.mGameMode == GameMode.TIME) {
            GameTimer = new GameTimer(mCurrentGameState.mGameTimeInMillis, 1000);
            GameTimer.start();
        }

    }

    /**
     * This method is called from the GameActivity.
     *
     * @param mIsStartingNewGame Set to true if the user wants to start a new
     *                           game. False if he wants to continue.
     */
    public void setIsStartingNewGame(boolean mIsStartingNewGame) {

        this.mIsStartingNewGame = mIsStartingNewGame;
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
     * This method is called from the CardFragment class when the user chooses
     * an attribute by clicking on it or from the AI task. It chooses the
     * clicked Attribute to be the one compared with the opponents one. It
     * compares the Attributes and determines who is the winner. This method
     * increases the winners points(+1) and rearranges both decks. This method
     * performs a fragment transaction. It replaces the current Fragment with a
     * GameCompareFragment!
     *
     * @param chosenAttr the attribute you want to compare
     */
    @Override
    public void chooseAttribute(Attribute chosenAttr) {

        GameCompareFragment compareFragment = GameCompareFragment.newInstance();
        //getting the users and ais value for the compared attribute
        float userAttributeValue = 0, aiAttributeValue = 0;
        RoundWinner winner;
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
                winner = RoundWinner.USER;
                mCurrentGameState.mIsUsersTurn = true;
            } else if (aiAttributeValue > userAttributeValue) {
                mCurrentGameState.mAIPoints++;
                winner = RoundWinner.AI;
                mCurrentGameState.mIsUsersTurn = false;
            } else {
                winner = RoundWinner.DRAW;
                mCurrentGameState.mIsUsersTurn = !mCurrentGameState.mIsUsersTurn;

            }
        } else {
            if (userAttributeValue > aiAttributeValue) {
                mCurrentGameState.mAIPoints++;
                winner = RoundWinner.AI;
                mCurrentGameState.mIsUsersTurn = false;
            } else if (aiAttributeValue > userAttributeValue) {
                mCurrentGameState.mUserPoints++;
                winner = RoundWinner.USER;
                mCurrentGameState.mIsUsersTurn = true;
            } else {
                winner = RoundWinner.DRAW;
                mCurrentGameState.mIsUsersTurn = !mCurrentGameState.mIsUsersTurn;

            }
        }
        //there are a lot of better ways to do this but i needed the change some
        // things and this was the way with the least coding effort ;)
        mUserCompareImage = setCompareImage(true);
        mAICompareImage = setCompareImage(false);
        mUserCompareValue = setCompareAttributeValue(true, chosenAttr);
        mAICompareValue = setCompareAttributeValue(false, chosenAttr);
        //rearranging both decks
        mDeckRearranger = new AsyncDeckRearranger();
        mDeckRearranger.execute(winner);
        //tell compareFragment who won and which attribute was compared to
        // display things correct.
        compareFragment.setRoundWinner(winner);
        compareFragment.setAttribute(chosenAttr);

        if (mCurrentGameState.mGameMode == GameMode.ROUNDS) {
            mCurrentGameState.mCurrentRound++;
        }
        mCurrentGameState.save();
        mBackEnd.switchToView(compareFragment);

    }


    /**
     * This method is called when the user clicks on the compare fragment. This
     * method performs a Fragment Transaction or calls a new Activity if the
     * game is finished.
     */
    @Override
    public void onClickCompare(RoundWinner winner) {

        Intent intent = new Intent(mCtx, GameEndActivity.class);
        boolean isFinish = false;
        if (mCurrentGameState.mGameMode == GameMode.POINTS) {
            if (mCurrentGameState.mAIPoints == mCurrentGameState.mMaxPoints ||
                    mCurrentGameState.mUserPoints == mCurrentGameState.mMaxPoints) {

                intent.putExtra(GameEndPresenter.WINNER, winner == RoundWinner
                        .USER ? GameEndState.WIN : GameEndState.LOSE);
                intent.putExtra(GameEndPresenter.SUB, " ");
                mDeckRearranger.cancel(true);
                mBackEnd.startActivity(intent);
                isFinish = true;

            }
        } else if (mCurrentGameState.mGameMode == GameMode.ROUNDS) {
            if (mCurrentGameState.mCurrentRound == mCurrentGameState.mMaxRounds) {
                intent.putExtra(GameEndPresenter.WINNER, mCurrentGameState
                        .mAIPoints > mCurrentGameState.mUserPoints ? GameEndState
                        .LOSE : mCurrentGameState
                        .mUserPoints > mCurrentGameState.mAIPoints ? GameEndState
                        .WIN : GameEndState.DRAW);
                intent.putExtra(GameEndPresenter.SUB, " ");
                mDeckRearranger.cancel(true);
                mBackEnd.startActivity(intent);
                isFinish = true;
            }
        } else if (mCurrentGameState.mGameMode == GameMode.INSANE) {

        }
        if (!isFinish) {
            GameFragment gameFragment = GameFragment.newInstance();
            mBackEnd.switchToView(gameFragment);
        }
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
     * Use this method to get the image of the currently compared cards.
     *
     * @param fromUser set to true if you want the image from the users card and
     *                 false and if you want the image of the ais deck
     * @return the image
     */
    @Override
    public Drawable getCompareImage(boolean fromUser) {

        return fromUser ? mUserCompareImage : mAICompareImage;
    }

    /**
     * Persisting the users and ais image of the top card which is currently
     * compared before rearranging the deck. So the compare fragment can show
     * them.
     *
     * @param fromUser true to save user image false for ais
     * @return the image as drawable
     */
    private Drawable setCompareImage(boolean fromUser) {

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
     * Use this method to get the value of the currently compared attribute.
     *
     * @param fromUser true if you want the value from the users card, false if
     *                 you want it from the ais card
     * @return the value of the given attribute or -1 if the attribute is not
     * found
     */
    @Override
    public float getCompareAttributeValue(boolean fromUser) {

        return fromUser ? mUserCompareValue : mAICompareValue;
    }

    /**
     * Setting the users or ais value of the currently compared card. This is
     * called before we rearrange the deck to persist the values for the compare
     * fragment.
     *
     * @param fromUser  true for users value false for ais
     * @param attribute the attribute which was compared
     * @return -1 if attribute is not found otherwise the attributes value
     */
    private float setCompareAttributeValue(boolean fromUser, Attribute
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
     * Use this method to start the AIs "thinking process" ;)
     */
    @Override
    public void startAI() {

        if (!mCurrentGameState.mIsUsersTurn) {
            mAI = new AI();
            mAI.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    public class AI extends AsyncTask<Void, Void, Attribute> {

        /**
         * This is the logic of the "AI". First of all it creates a list for
         * each attribute with the values occurring in the current deck. After
         * that it sorts the list descending or ascending, depending if
         * Attribute.mLargerWins is true or not. Now it checks the first
         * appearance of the own value for each attribute in the sorted lists.
         * Finally it looks for the highest positions. In hard mode it chooses
         * the two best values as possible candidates and randomly takes one of
         * them. In medium mode the three best values and in easy mode the four
         * best values.
         *
         * @return the Attribute chosen by the AI
         */
        @Override
        protected Attribute doInBackground(Void... params) {
            //TODO: get the current deck from intent or preferences
            Deck currentDeck = Deck.findById(Deck.class, 1);
            List<Card> currentDeckCards = currentDeck.getCards();
            List<List<Float>> attributeLists = new ArrayList<>();
            GameCard currentCard = mCurrentGameState.getAIDeck().get(0);

            for (Attribute attr : currentDeck.getAttributes()) {
                attributeLists.add(new ArrayList<Float>());
            }
            for (Card currentDeckCard : currentDeckCards) {
                int i = 0;
                for (AttributeValue attributeValue : currentDeckCard.getAttributeValues()) {
                    attributeLists.get(i).add(attributeValue.mValue);
                    i++;
                }
            }
            int i = 0;
            for (Attribute attr : currentDeck.getAttributes()) {
                Collections.sort(attributeLists.get(i));
                if (!attr.mLargerWins) {
                    Collections.reverse(attributeLists.get(i));
                }
                i++;
            }
            Log.d("AttributeLists: ", attributeLists.toString());
            List<Integer> ownAttributePositions = new ArrayList<>();
            i = 0;
            for (AttributeValue attributeValue : currentCard.mCard.getAttributeValues()) {
                ownAttributePositions.add(attributeLists.get(i).indexOf
                        (attributeValue.mValue));
                i++;
            }
            Log.d("PositionList: ", ownAttributePositions.toString());
            int[] attributeCandidates = new int[2];
            for (int j = 0; j < 2; j++) {
                i = 0;
                attributeCandidates[j] = 0;
                for (Integer ownAttributePosition : ownAttributePositions) {
                    if (ownAttributePosition > ownAttributePositions.get
                            (attributeCandidates[j])) {
                        attributeCandidates[j] = i;
                    }
                    i++;
                }
                ownAttributePositions.remove(attributeCandidates[0]);
                ownAttributePositions.add(attributeCandidates[0], 0);
            }
            if (isCancelled()) {
                return null;
            }
            Log.d("AttributeCandidatePos: ", attributeCandidates
                    .toString());
            Random random = new Random();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            return currentCard.mCard.getAttributeValues().get
                    (attributeCandidates[random.nextInt(2)]).mAttribute;

        }

        /**
         * This method actually "clicks" on the attribute chosen in
         * doInBackground(). But if the User pressed the back button in between
         * it simply does nothing.
         *
         * @param attribute the chosen attribute
         */
        @Override
        protected void onPostExecute(Attribute attribute) {

            if (attribute != null && !isCancelled()) {
                chooseAttribute(attribute);
            }

        }
    }

    public class GameTimer extends CountDownTimer {

        public GameTimer(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            mCurrentGameState.mCurrentTimeInMillis = mCurrentGameState
                    .mGameTimeInMillis - millisUntilFinished;
            mView.updateGameTime(mCurrentGameState
                    .mGameTimeInMillis - mCurrentGameState
                    .mCurrentTimeInMillis);
        }

        @Override
        public void onFinish() {

            Intent intent = new Intent(mCtx, GameEndActivity.class);
            intent.putExtra(GameEndPresenter.WINNER, mCurrentGameState
                    .mAIPoints > mCurrentGameState.mUserPoints ? GameEndState
                    .LOSE : mCurrentGameState
                    .mUserPoints > mCurrentGameState.mAIPoints ? GameEndState
                    .WIN : GameEndState.DRAW);
            intent.putExtra(GameEndPresenter.SUB, " ");
            if (mDeckRearranger != null && !mDeckRearranger.isCancelled()) {
                mDeckRearranger.cancel(true);
            }
            mBackEnd.startActivity(intent);
        }
    }

    /**
     * Loading the Users and AIs Decks into DB. This is a fucking long task
     * because we have to save them redundant because sugar orm can handle
     * neither lists nor arrays.
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

    /**
     * This class is used to rearrange the users and ais deck after each round.
     */
    private class AsyncDeckRearranger extends AsyncTask<RoundWinner, Void, Void> {

        @Override
        protected Void doInBackground(RoundWinner... params) {
            //params[0] == RoundWinner winner
            //remove first card from loser deck and add it to winner deck
            changeLostCardOwner(params[0]);
            if (params[0] == RoundWinner.USER) {
                rearrangeWinnerOrDrawDeck(mCurrentGameState.getUserDeck());
                rearrangeLoserDeck(mCurrentGameState.getAIDeck());

                Log.d("DECKS: ", mCurrentGameState.getAIDeck().toString());
                Log.d("DECKS: ", mCurrentGameState.getUserDeck().toString());
            } else if (params[0] == RoundWinner.AI) {
                rearrangeWinnerOrDrawDeck(mCurrentGameState.getAIDeck());
                rearrangeLoserDeck(mCurrentGameState.getUserDeck());

                Log.d("DECKS: ", mCurrentGameState.getAIDeck().toString());
                Log.d("DECKS: ", mCurrentGameState.getUserDeck().toString());
            } else {
                rearrangeWinnerOrDrawDeck(mCurrentGameState.getAIDeck());
                rearrangeWinnerOrDrawDeck(mCurrentGameState.getUserDeck());
            }
            return null;
        }

        /**
         * Use this method to rearrange the Deck of a winner or from both if the
         * result was a draw. IMPORTANT: if you want to rearrange a winners deck
         * don't forget to call changeLostCardOwner(RoundWinner winner) before!
         *
         * @param winnerDeck the deck you want to rearrange
         */
        private void rearrangeWinnerOrDrawDeck(List<GameCard> winnerDeck) {

            if (!isCancelled()) {
                winnerDeck.get(0).mPositionInDeck = winnerDeck.size();
                if (!isCancelled()) {
                    winnerDeck.get(0).save();
                    for (GameCard gameCard : winnerDeck) {
                        if (isCancelled()) {
                            break;
                        }
                        gameCard.mPositionInDeck--;
                        gameCard.save();

                    }
                }
            }
        }

        /**
         * Use this method to rearrange the deck of a loser. IMPORTANT: dont'
         * forget to call changeLostCardOwner(RoundWinner winner) BEFORE!
         *
         * @param loserDeck the deck you want to rearrange
         */
        private void rearrangeLoserDeck(List<GameCard> loserDeck) {

            if (!isCancelled()) {
                for (GameCard gameCard : loserDeck) {
                    if (isCancelled()) {
                        break;
                    }
                    gameCard.mPositionInDeck--;
                    gameCard.save();
                }
            }
        }

        /**
         * Use this method to change the owner of the currently played card.
         *
         * @param winner determines who gets the card and who loses it.
         */
        private void changeLostCardOwner(RoundWinner winner) {

            if (!isCancelled()) {
                GameCard lostCard = winner == RoundWinner.USER ? mCurrentGameState
                        .getAIDeck().get(0) : mCurrentGameState.getUserDeck().get(0);
                lostCard.mOwner = winner == RoundWinner.USER ? "user" : "ai";
                lostCard.mPositionInDeck = winner == RoundWinner
                        .USER ? mCurrentGameState.getUserDeck().size() : mCurrentGameState
                        .getAIDeck().size();

                lostCard.save();
            }
        }
    }
}
