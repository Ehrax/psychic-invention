package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

import de.in.uulm.map.quartett.gamesettings.GameLevel;
import de.in.uulm.map.quartett.gamesettings.GameMode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by maxka on 08.01.2017. This holds the State of the current local
 * game.
 */

public class LocalGameState extends SugarRecord {

    public String mUserName;
    public long mDeckID;
    public int mUserPoints;
    public int mAIPoints;
    public long mCurrentRound;
    public long mCurrentTimeInMillis;
    public long mLimit;
    public boolean mIsUsersTurn;
    public GameMode mGameMode;
    public GameLevel mGameLevel;

    public LocalGameState() {

    }

    /**
     * Simple constructor setting the gameMode and respectively the maxTime,
     * maxRounds or maxPoints.
     *
     * @param gameLimit the maxRounds,maxPoints or time limit in milliseconds
     *                  depending on the mode you want to play
     * @param gameMode  the game mode
     * @param userName  the name of the current user
     */
    public LocalGameState(long gameLimit, GameMode gameMode,GameLevel level,
                          long deckID,
                          String
                          userName) {

        mGameMode = gameMode;
        mLimit = gameLimit;
        mCurrentRound = 0;
        mCurrentTimeInMillis = 0;
        mUserPoints = 0;
        mAIPoints = 0;
        mIsUsersTurn = true;
        mDeckID=deckID;
        mUserName = userName;
        mGameLevel=level;
    }

    /**
     * Use this method to get a list of all Cards the User has in his deck.
     *
     * @return The users deck
     */
    public List<GameCard> getUserDeck() {

        List<GameCard> userDeck = GameCard.find(GameCard.class, "m_game_state " +
                "= ? and " + "m_owner = ?", "" + this.getId(), "user");
        Collections.sort(userDeck, new Comparator<GameCard>() {
            @Override
            public int compare(GameCard o1, GameCard o2) {

                return Integer.compare(o1.mPositionInDeck, o2.mPositionInDeck);
            }
        });
        return userDeck;
    }

    /**
     * Use this method to get a list of all Cards the AI has in its deck.
     */
    public List<GameCard> getAIDeck() {

        List<GameCard> aiDeck = GameCard.find(GameCard.class, "m_game_state =" +
                " ? and " + "m_owner = ?", "" + this.getId(), "ai");
        Collections.sort(aiDeck, new Comparator<GameCard>() {
            @Override
            public int compare(GameCard o1, GameCard o2) {

                return Integer.compare(o1.mPositionInDeck, o2.mPositionInDeck);
            }
        });
        return aiDeck;
    }

}
