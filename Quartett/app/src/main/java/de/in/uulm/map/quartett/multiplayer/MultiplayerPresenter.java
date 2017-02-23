package de.in.uulm.map.quartett.multiplayer;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.internal.api.MultiplayerImpl;
import com.google.android.gms.games.internal.api.TurnBasedMultiplayerImpl;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchBuffer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.example.games.basegameutils.BaseGameUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.gallery.CardFragment;
import de.in.uulm.map.quartett.game.RoundWinner;
import de.in.uulm.map.quartett.util.AssetUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by maxka on 29.01.2017.
 */

public class MultiplayerPresenter implements MultiplayerContract.Presenter,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient
                .OnConnectionFailedListener, OnTurnBasedMatchUpdateReceivedListener {

    private GoogleApiClient mGoogleApiClient;

    private MultiplayerContract.View mView;
    private MultiplayerContract.Backend mBackend;
    private MultiplayerContract.Model mModel;
    private Context mContext;

    private TurnData mCurrentTurnData;
    private TurnBasedMatch mCurrentTurnBasedMatch;
    private MultiplayerTurnWinner mCurrentTurnWinner;
    private Attribute mCurrentComparedAttribute;
    private MatchesLoadedCallback mMatchesLoadedCallback;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;

    public MultiplayerPresenter(MultiplayerContract.View view,
                                MultiplayerContract.Backend backend,
                                MultiplayerContract.Model model, Context
                                        ctx) {

        mView = view;
        mContext = ctx;
        mBackend = backend;
        mModel = model;
    }

    @Override
    public GoogleApiClient getClient() {

        return mGoogleApiClient;
    }

    @Override
    public void setView(MultiplayerContract.View view) {

        mView = view;
    }

    @Override
    public void start() {
        //Create the Google Api Client
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

    }

    @Override
    public void disconnectFromGoogleGames() {

        mGoogleApiClient.disconnect();

    }

    @Override
    public void connectToGoogleGames() {

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mView.replaceProgressBar();
        Games.TurnBasedMultiplayer.registerMatchUpdateListener
                (mGoogleApiClient, this);

        mMatchesLoadedCallback = new MatchesLoadedCallback();
        int[] gameStatus = {TurnBasedMatch.MATCH_STATUS_ACTIVE,
                TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING,
                TurnBasedMatch.MATCH_STATUS_COMPLETE};
        Games.TurnBasedMultiplayer.loadMatchesByStatus(mGoogleApiClient,
                gameStatus).setResultCallback(mMatchesLoadedCallback);


    }


    @Override
    public void onConnectionSuspended(int i) {

        connectToGoogleGames();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        mResolvingConnectionFailure = true;

        if (!mBackend.resolveConnectionFailure(mGoogleApiClient, connectionResult)) {
            mResolvingConnectionFailure = false;
        }


    }

    @Override
    public void onResolveConnectionFailureResult(int resultCode, int requestCode) {

        mResolvingConnectionFailure = false;
        if (resultCode == -1) {
            mGoogleApiClient.connect();
        } else {
            // Bring up an error dialog to alert the user that sign-in
            // failed.
            mBackend.showActivityResultError(requestCode, resultCode);

        }
    }

    @Override
    public void startSelectOpponent() {

        mBackend.startSelectOpponent(mGoogleApiClient);

    }


    /**
     * Creating a new turn based multi player match.
     *
     * @param data Intent from the choose player activity which holds all
     *             information about the chosen opponent(player id or if the
     *             player want to auto match)
     */
    @Override
    public void createMatch(Intent data) {
        // Get the invitee list.
        final ArrayList<String> invitees =
                data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

        // Get auto-match criteria.
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(
                Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(
                Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
        } else {
            autoMatchCriteria = null;
        }


        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                .addInvitedPlayers(invitees)
                .setAutoMatchCriteria(autoMatchCriteria)
                .build();

        // Create and start the match.
        Games.TurnBasedMultiplayer
                .createMatch(mGoogleApiClient, tbmc)
                .setResultCallback(new MatchInitiatedCallback());


    }

    @Override
    public Card getCard(long deckId, int position) {

        return Deck.findById(Deck.class, deckId).getCards().get(position);
    }

    @Override
    public TurnBasedMatch getCurrentTurnBasedMatch() {

        return mCurrentTurnBasedMatch;
    }

    /**
     * This method is used to indicate that a player already saw the compare
     * screen. This is used to handle situations where a player took a turn and
     * the other player was not ingame so he can see the compare screen if he
     * opens the game again but you want the user to only see this screen once.
     */
    @Override
    public void setSawCompare(boolean sawCompare) {

        mCurrentTurnData.setSawCompare(sawCompare);
        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient,
                mCurrentTurnBasedMatch.getMatchId(), TurnData.convertToBytes
                        (mCurrentTurnData), getOwnParticipantID());
    }

    /**
     * This method is called if the player selects a match from the match
     * overview. It is loading the game data if there is one available.
     * Otherwise if there is no game data that means we created that game so it
     * initializes the game data such as shuffle the cards and so on.
     *
     * @param match the clicked match
     */
    @Override
    public void startSelectedGame(TurnBasedMatch match) {

        mCurrentTurnBasedMatch = match;
        long deckID = 1;
        MultiplayerGameFragment mpFragment = MultiplayerGameFragment
                .newInstance();
        //deserialize match data if there is one
        if (match.getData() != null) {

            mCurrentTurnData = TurnData.convertFromBytes(match.getData());
            if (mCurrentTurnData.getSecondPlayerID() == null) {
                mCurrentTurnData.setSecondPlayerID(Games.Players
                        .getCurrentPlayerId(mGoogleApiClient));
            }
            if (mCurrentComparedAttribute == null) {
                mCurrentComparedAttribute = Attribute.findById
                        (Attribute.class, mCurrentTurnData.getChosenAttributeID());
            }
            if (!mCurrentTurnData.getSawCompare() && mCurrentTurnBasedMatch
                    .getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
                mBackend.switchToView(MultiplayerGameCompareFragment.newInstance());
            } else {
                mBackend.switchToView(mpFragment);
            }

        } else {
            //otherwise we are the first player so initialize the game
            byte[] shuffledDeck = shuffleDeck(deckID);
            byte[] thisPlayerDeck = new byte[shuffledDeck.length / 2];
            byte[] otherPlayerDeck = new byte[shuffledDeck
                    .length - thisPlayerDeck.length];
            System.arraycopy(shuffledDeck, 0, thisPlayerDeck, 0, thisPlayerDeck.length);
            System.arraycopy(shuffledDeck, thisPlayerDeck.length,
                    otherPlayerDeck, 0, otherPlayerDeck.length);
            String thisPlayerID = Games.Players.getCurrentPlayerId
                    (mGoogleApiClient);
            String otherPlayerID = null;
            List<String> participantIds = match.getParticipantIds();
            if (participantIds.size() > 1) {
                for (String participantId : participantIds) {
                    if (participantId != thisPlayerID) {
                        otherPlayerID = participantId;
                        break;
                    }
                }
            }
            mCurrentTurnData = new TurnData(deckID, thisPlayerDeck,
                    otherPlayerDeck,
                    thisPlayerID, otherPlayerID);

            mBackend.switchToView(mpFragment);

        }
    }

    /**
     * This method is called if a player chooses a attribute to be the compared
     * one. This method actually does all the work like checking who won this
     * game counting the points and rearranging the decks. It also serializes
     * the CurrentTurnData and calls the TurnBasedMultiplayer .takeTurn()
     * method.
     *
     * @param chosenAttribute the Attribute which is chosen to be the one to
     *                        comapare with the other player
     */
    @Override
    public void chooseAttribute(Attribute chosenAttribute) {

        mCurrentComparedAttribute = chosenAttribute;
        mCurrentTurnData.setChosenAttributeID(chosenAttribute.getId());
        mCurrentTurnData.incrementCurrentTurn();
        checkWinner(chosenAttribute, getPlayerAttributeValue(true,
                chosenAttribute), getPlayerAttributeValue(false, chosenAttribute));

        byte[] cardsToCompare = {mCurrentTurnData.getFirstPlayerDeck()[0],
                mCurrentTurnData.getSecondPlayerDeck()[0]};
        mCurrentTurnData.setComparedCards(cardsToCompare);
        mCurrentTurnData.setSawCompare(false);

        rearrangeDecks();


        byte[] turnData = TurnData.convertToBytes(mCurrentTurnData);

        int isGameOver = isGameOver();

        if (isGameOver == -1) {
            Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, mCurrentTurnBasedMatch
                    .getMatchId(), turnData, getNextParticipant(null))
                    .setResultCallback(new
                            TookTurnCallback());
        } else {

            Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient,
                    mCurrentTurnBasedMatch.getMatchId(), turnData,
                    createResult(isGameOver)).setResultCallback(new TookTurnCallback());
        }


    }

    /**
     * Use this method to check if the game is over and who won if it is over.
     *
     * @return -1 if the game is not over, 1 if first player won, 2 if second
     * player won and 0 if the game ended in a draw
     */
    private int isGameOver() {

        if (mCurrentTurnData.getCurrentTurn() < 10) {
            return -1;
        }

        if (mCurrentTurnData.getFirstPlayerPoints() > mCurrentTurnData
                .getSecondPlayerPoints()) {
            return 1;
        } else if (mCurrentTurnData.getSecondPlayerPoints() > mCurrentTurnData
                .getFirstPlayerPoints()) {
            return 2;
        }
        return 0;
    }

    /**
     * Use this method to create a participant result to finish the game.
     *
     * @param whoWon pass in 0 for a tie, 1 if the first player won and 2 if the
     *               second player won.
     * @return the participant result including the participant id of this
     * player, the information if this player won, lost or tied this game and
     * the players ranking in this game.
     */
    private ParticipantResult createResult(int whoWon) {

        if (whoWon == 0) {
            return new ParticipantResult(getOwnParticipantID(),
                    ParticipantResult.MATCH_RESULT_TIE, 1);
        } else if (whoWon == 1) {
            return new ParticipantResult(getOwnParticipantID(),
                    isThisPlayerFirstPlayer() ? ParticipantResult
                            .MATCH_RESULT_WIN : ParticipantResult
                            .MATCH_RESULT_LOSS, isThisPlayerFirstPlayer() ? 1 : 2);
        } else {
            return new ParticipantResult(getOwnParticipantID(),
                    isThisPlayerFirstPlayer() ? ParticipantResult
                            .MATCH_RESULT_LOSS : ParticipantResult
                            .MATCH_RESULT_WIN, isThisPlayerFirstPlayer() ? 2 : 1);
        }
    }

    /**
     * Use this method to get the participant id of the current player
     *
     * @return the participant id of the current player or null if the current
     * user id can not be found in the participants list.
     */
    private String getOwnParticipantID() {

        List<Participant> participants = mCurrentTurnBasedMatch
                .getParticipants();
        for (Participant participant : participants) {
            if (participant.getDisplayName().equals(Games.Players
                    .getCurrentPlayer(mGoogleApiClient).getDisplayName())) {
                return participant.getParticipantId();
            }
        }
        return null;
    }

    /**
     * Use this method to get the participant id if the next player.
     *
     * @param match the match you want to get the next participant or null if
     *              you want to use the mCurrentTurnBasedMatch instead
     * @return the participant id of the next player or null if there is no next
     * player.
     */
    private String getNextParticipant(TurnBasedMatch match) {

        if (match == null) {
            match = mCurrentTurnBasedMatch;
        }
        List<Participant> participants = match
                .getParticipants();
        for (Participant participant : participants) {
            if (!participant.getDisplayName().equals(Games.Players
                    .getCurrentPlayer(mGoogleApiClient).getDisplayName())) {
                return participant.getParticipantId();
            }
        }
        return null;
    }

    /**
     * Use this method to check who won this turn.
     *
     * @param chosenAttribute       the attribute to compare
     * @param firstPlayerAttribute  the compare attribute from the first players
     *                              card
     * @param secondPlayerAttribute the compare attribute from the second
     *                              players card
     */
    private void checkWinner(Attribute chosenAttribute,
                             AttributeValue firstPlayerAttribute,
                             AttributeValue secondPlayerAttribute) {

        if (chosenAttribute.mLargerWins) {
            if (firstPlayerAttribute.mValue > secondPlayerAttribute.mValue) {
                mCurrentTurnData.incrementFirstPlayerPoints();
                mCurrentTurnData.setCurrentTurnWinner(MultiplayerTurnWinner
                        .FIRST);

            } else if (secondPlayerAttribute.mValue > firstPlayerAttribute.mValue) {
                mCurrentTurnData.incrementSecondPlayerPoints();
                mCurrentTurnData.setCurrentTurnWinner(MultiplayerTurnWinner
                        .SECOND);
            } else {
                mCurrentTurnData.setCurrentTurnWinner(MultiplayerTurnWinner
                        .DRAW);
            }
        } else {
            if (firstPlayerAttribute.mValue < secondPlayerAttribute.mValue) {
                mCurrentTurnData.incrementFirstPlayerPoints();
                mCurrentTurnData.setCurrentTurnWinner(MultiplayerTurnWinner
                        .FIRST);

            } else if (secondPlayerAttribute.mValue < firstPlayerAttribute.mValue) {
                mCurrentTurnData.incrementSecondPlayerPoints();
                mCurrentTurnData.setCurrentTurnWinner(MultiplayerTurnWinner
                        .SECOND);
            } else {
                mCurrentTurnData.setCurrentTurnWinner(MultiplayerTurnWinner
                        .DRAW);
            }
        }
    }

    /**
     * Use this method to rearrange both players decks after each turn.
     */
    private void rearrangeDecks() {

        List<Byte> firstPlayerDeck = new ArrayList<>();
        byte[] firstPlayerDeckArray = mCurrentTurnData.getFirstPlayerDeck();
        for (byte b : firstPlayerDeckArray) {
            firstPlayerDeck.add(b);
        }
        List<Byte> secondPlayerDeck = new ArrayList<>();
        byte[] secondPlayerDeckArray = mCurrentTurnData.getSecondPlayerDeck();
        for (byte b : secondPlayerDeckArray) {
            secondPlayerDeck.add(b);
        }
        if (mCurrentTurnWinner == MultiplayerTurnWinner.FIRST) {
            firstPlayerDeck.add(secondPlayerDeck.get(0));
            firstPlayerDeck.add(firstPlayerDeck.get(0));
            firstPlayerDeck.remove(0);
            secondPlayerDeck.remove(0);
        } else if (mCurrentTurnWinner == MultiplayerTurnWinner.SECOND) {
            secondPlayerDeck.add(firstPlayerDeck.get(0));
            secondPlayerDeck.add(secondPlayerDeck.get(0));
            secondPlayerDeck.remove(0);
            firstPlayerDeck.remove(0);
        } else {
            firstPlayerDeck.add(firstPlayerDeck.get(0));
            firstPlayerDeck.remove(0);
            secondPlayerDeck.add(secondPlayerDeck.get(0));
            secondPlayerDeck.remove(0);
        }
        int i = 0;
        firstPlayerDeckArray = new byte[firstPlayerDeck.size()];
        for (Byte aByte : firstPlayerDeck) {
            firstPlayerDeckArray[i] = aByte;
            i++;
        }
        i = 0;
        secondPlayerDeckArray = new byte[secondPlayerDeck.size()];
        for (Byte aByte : secondPlayerDeck) {
            secondPlayerDeckArray[i] = aByte;
            i++;
        }
        mCurrentTurnData.setFirstPlayerDeck(firstPlayerDeckArray);
        mCurrentTurnData.setSecondPlayerDeck(secondPlayerDeckArray);
    }


    private AttributeValue getPlayerAttributeValue(boolean fromFirstPlayer,
                                                   Attribute chosenAttribute) {

        Card currentCard;
        if (fromFirstPlayer) {
            currentCard = Card.find(Card.class, "m_deck = ? and " +
                    "m_position = ?", mCurrentTurnData
                    .getDeckID() + "", mCurrentTurnData.getFirstPlayerDeck()[0] + "")
                    .get(0);
        } else {
            currentCard = Card.find(Card.class, "m_deck = ? and " +
                    "m_position = ?", mCurrentTurnData
                    .getDeckID() + "", mCurrentTurnData.getSecondPlayerDeck()[0] + "")
                    .get(0);
        }
        List<AttributeValue> currentValues = currentCard.getAttributeValues();
        for (AttributeValue currentValue : currentValues) {
            if (currentValue.mAttribute.mName.equals(chosenAttribute.mName)) {
                return currentValue;
            }
        }

        return null;

    }

    @Override
    public AttributeValue getPlayerCompareAttributeValue(boolean fromThisPlayer) {

        Card currentCard;
        if (fromThisPlayer) {
            currentCard = Card.find(Card.class, "m_deck = ? and " +
                    "m_position = ?", mCurrentTurnData
                    .getDeckID() + "", mCurrentTurnData.getComparedCards()
                    [isThisPlayerFirstPlayer() ? 0 : 1] + "").get(0);
        } else {
            currentCard = Card.find(Card.class, "m_deck = ? and " +
                    "m_position = ?", mCurrentTurnData
                    .getDeckID() + "", mCurrentTurnData.getComparedCards()
                    [isThisPlayerFirstPlayer() ? 1 : 0] + "").get(0);
        }

        List<AttributeValue> currentValues = currentCard.getAttributeValues();
        for (AttributeValue currentValue : currentValues) {
            if (currentValue.mAttribute.mName.equals(mCurrentComparedAttribute.mName)) {
                return currentValue;
            }
        }

        return null;

    }

    @Override
    public MultiplayerTurnWinner getWinner() {

        return mCurrentTurnWinner;
    }

    /**
     * Use this method to determine if the current player is saved as first
     * player or second player
     *
     * @return true if this player is saved as first player and otherwise false
     */
    @Override
    public boolean isThisPlayerFirstPlayer() {

        return Games.Players.getCurrentPlayerId(mGoogleApiClient).equals
                (mCurrentTurnData.getFirstPlayerID());
    }

    @Override
    public void onClickCompare() {

        if (mCurrentTurnBasedMatch.getStatus() == TurnBasedMatch.MATCH_STATUS_COMPLETE) {
            if (mCurrentTurnBasedMatch.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN){
                Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient,
                        mCurrentTurnBasedMatch.getMatchId()).setResultCallback(new
                                FinishedGameCallback());
            }else {
                mView.makeSnackBar("Game End");
            }
        } else {
            MultiplayerGameFragment gameFragment = MultiplayerGameFragment
                    .newInstance();
            mBackend.switchToView(gameFragment);
        }
    }

    @Override
    public Attribute getCurrentAttribute() {

        return mCurrentComparedAttribute;
    }

    @Override
    public void setCompareImage(ImageView imgView, boolean fromThisPlayer) {

        Card comparedCard;
        if (fromThisPlayer) {
            comparedCard = Card.find(Card.class, "m_deck = ? and " +
                    "m_position = ?", "" + mCurrentTurnData.getDeckID(), "" + mCurrentTurnData
                    .getComparedCards()[isThisPlayerFirstPlayer() ? 0 : 1]).get(0);
        } else {
            comparedCard = Card.find(Card.class, "m_deck = ? and " +
                    "m_position = ?", "" + mCurrentTurnData.getDeckID(), "" + mCurrentTurnData
                    .getComparedCards()[isThisPlayerFirstPlayer() ? 1 : 0])
                    .get(0);
        }
        String imageUri = comparedCard.getCardImages().get(0).mImage.mUri;
        if (imageUri.contains("android_asset")) {
            imgView.setImageDrawable(AssetUtils.getDrawableFromAssetUri
                    (mContext, Uri.parse(imageUri)));

        } else {
            imgView.setImageURI(Uri.parse(mContext.getFilesDir() + File
                    .separator + imageUri));
        }


    }

    @Override
    public CardFragment createCurrentCardFragment() {

        CardFragment currentCard = CardFragment.newInstance();
        currentCard.setDeckId(mCurrentTurnData.getDeckID());
        currentCard.setPosition(isThisPlayerFirstPlayer() ? mCurrentTurnData.getFirstPlayerDeck()[0]
                : mCurrentTurnData
                .getSecondPlayerDeck()[0]);
        currentCard.setMultiplayerPresenter(this);
        return currentCard;
    }

    @Override
    public String getOpponentName() {

        if (mCurrentTurnBasedMatch == null) {
            return null;
        } else if (mCurrentTurnBasedMatch.getParticipantIds().size() < 2) {
            return "Anonymous";
        }

        List<Participant> participants = mCurrentTurnBasedMatch
                .getParticipants();
        for (Participant participant : participants) {
            if (!participant.getDisplayName().equals(Games.Players
                    .getCurrentPlayer(mGoogleApiClient).getDisplayName())) {
                return participant.getDisplayName();
            }
        }

        //if we got this far something went wrong xD
        return null;
    }

    @Override
    public void releaseAllBuffer() {

        if (mMatchesLoadedCallback != null) {
            mMatchesLoadedCallback.releaseAllBuffers();
        }
    }

    @Override
    public byte getOtherPlayersPoints() {

        return isThisPlayerFirstPlayer() ? mCurrentTurnData
                .getSecondPlayerPoints() : mCurrentTurnData.getFirstPlayerPoints();
    }

    @Override
    public byte getThisPlayersPoints() {

        return isThisPlayerFirstPlayer() ? mCurrentTurnData
                .getFirstPlayerPoints() : mCurrentTurnData.getSecondPlayerPoints
                ();
    }


    private byte[] shuffleDeck(long deckID) {

        Deck currentDeck = Deck.findById(Deck.class, deckID);
        List<Card> cards = currentDeck.getCards();
        Collections.shuffle(cards);
        byte[] shuffledDeck = new byte[cards.size()];
        int i = 0;
        for (Card card : cards) {
            shuffledDeck[i] = (byte) card.mPosition;
            i++;
        }
        return shuffledDeck;
    }

    @Override
    public void leaveMatch(TurnBasedMatch match) {

        if (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
            new TurnBasedMultiplayerImpl().leaveMatchDuringTurn
                    (mGoogleApiClient, match.getMatchId(), getNextParticipant
                            (match));
        } else {
            new TurnBasedMultiplayerImpl().leaveMatch(mGoogleApiClient, match
                    .getMatchId());
        }
    }

    @Override
    public TurnData getCurrentTurnData() {

        return mCurrentTurnData;
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch turnBasedMatch) {

        if (mCurrentTurnBasedMatch != null) {
            if (turnBasedMatch.getMatchId().equals(mCurrentTurnBasedMatch.getMatchId())) {
                mCurrentTurnBasedMatch = turnBasedMatch;
                mCurrentTurnData = TurnData.convertFromBytes(turnBasedMatch.getData
                        ());
                if (mCurrentTurnData != null) {
                    mCurrentComparedAttribute = Attribute.findById(Attribute
                            .class, mCurrentTurnData.getChosenAttributeID());

                    if (mCurrentTurnBasedMatch.getTurnStatus() == TurnBasedMatch
                            .MATCH_TURN_STATUS_MY_TURN) {
                        MultiplayerGameCompareFragment compareFragment =
                                MultiplayerGameCompareFragment.newInstance();
                        Fragment currentFragment = mBackend.getActivity()
                                .getSupportFragmentManager().findFragmentById
                                        (R.id.contentFrame);
                        if(currentFragment instanceof MultiplayerGameCompareFragment){
                            compareFragment.setDelayedEnterTransition(true);
                        }

                        mBackend.switchToView(compareFragment);
                    }
                }
            }
        }
    }

    @Override
    public void onTurnBasedMatchRemoved(String s) {

    }

    /**
     * This callback is used to initiate the game after it is created.
     */
    public class MatchInitiatedCallback implements
            ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> {

        @Override
        public void onResult(@NonNull TurnBasedMultiplayer
                .InitiateMatchResult result) {

            Status status = result.getStatus();
            if (!status.isSuccess()) {
                Log.d("Initiate Game:", "ERROR");
                return;
            }

            TurnBasedMatch match = result.getMatch();

            //add the match to the recycler view
            mModel.getMatchList().add(0, match);
            mModel.notifyAddedItem(0);


        }
    }

    /**
     * Adds the loaded matches to the recycler view.
     */
    public class MatchesLoadedCallback implements
            ResultCallback<TurnBasedMultiplayer.LoadMatchesResult> {

        private TurnBasedMatchBuffer myTurnMatches;
        private TurnBasedMatchBuffer theirTurnMatches;
        private TurnBasedMatchBuffer completedMatches;

        @Override
        public void onResult(@NonNull TurnBasedMultiplayer.LoadMatchesResult loadMatchesResult) {

            ArrayList<TurnBasedMatch> matches = mModel.getMatchList();

            //Uncomment this if you want to delete all current matches of
            // this user due to data structure changes

            /*for(TurnBasedMatch match : loadMatchesResult.getMatches().getMyTurnMatches()){
                Games.TurnBasedMultiplayer.dismissMatch(mGoogleApiClient,
                        match.getMatchId());
            }
            for(TurnBasedMatch match :loadMatchesResult.getMatches().getTheirTurnMatches()){
                Games.TurnBasedMultiplayer.leaveMatch(mGoogleApiClient,match
                        .getMatchId());
            }*/

            myTurnMatches = loadMatchesResult.getMatches()
                    .getMyTurnMatches();
            if (myTurnMatches != null) {
                for (TurnBasedMatch match : myTurnMatches) {
                    if (mCurrentTurnBasedMatch != null && mCurrentTurnBasedMatch
                            .getMatchId().equals(match.getMatchId())) {
                        mCurrentTurnBasedMatch = match;
                    }
                    matches.add(match);

                }

            }

            theirTurnMatches = loadMatchesResult
                    .getMatches().getTheirTurnMatches();
            if (theirTurnMatches != null) {
                for (TurnBasedMatch match : theirTurnMatches) {
                    if (mCurrentTurnBasedMatch != null && mCurrentTurnBasedMatch
                            .getMatchId().equals(match.getMatchId())) {
                        mCurrentTurnBasedMatch = match;
                    }
                    matches.add(match);
                }

            }

            completedMatches = loadMatchesResult
                    .getMatches().getCompletedMatches();
            if (completedMatches != null) {
                for (TurnBasedMatch match : completedMatches) {
                    if (mCurrentTurnBasedMatch != null && mCurrentTurnBasedMatch
                            .getMatchId().equals(match.getMatchId())) {
                        mCurrentTurnBasedMatch = match;
                    }
                    matches.add(match);
                }

            }

            mModel.update();
            //loadMatchesResult.release();

        }

        public void releaseAllBuffers() {

            if (theirTurnMatches != null) {
                theirTurnMatches.release();
            }

            if (myTurnMatches != null) {
                myTurnMatches.release();
            }
            if (completedMatches != null) {
                completedMatches.release();
            }
        }

    }

    public class TookTurnCallback implements
            ResultCallback<TurnBasedMultiplayer.UpdateMatchResult> {

        @Override
        public void onResult(@NonNull TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {

            mCurrentTurnBasedMatch = updateMatchResult.getMatch();

            MultiplayerGameCompareFragment mpCompareFragment =
                    MultiplayerGameCompareFragment.newInstance();
            mBackend.switchToView(mpCompareFragment);


        }
    }

    public class FinishedGameCallback implements
            ResultCallback<TurnBasedMultiplayer.UpdateMatchResult> {

        @Override
        public void onResult(@NonNull TurnBasedMultiplayer.UpdateMatchResult updateMatchResult) {

            mView.makeSnackBar("finished GAME");


        }
    }
}
