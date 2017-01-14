package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

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
    public int mUserPoints;
    public int mAIPoints;
    public int mCurrentRound;
    public int mCurrentTimeInMillis;
    public long mGameTimeInMillis;
    public int mMaxPoints;
    public int mMaxRounds;
    public boolean mIsUsersTurn;
    public GameMode mGameMode;

    public LocalGameState() {

    }

    /**
     * Simple constructor setting the gameMode and respectively the maxTime,
     * maxRounds or maxPoints.
     *
     * @param timeInMillis the game time in milliseconds or 0 if gameMode !=
     *                     HighscoreType.Time
     * @param maxPoints    the points the players have to reach to win the game
     *                     in point mode. Set it to 0 if game mode != point
     * @param maxRounds    the rounds to play until the player with most points
     *                     wins. Set to 0 if game mode != rounds
     * @param gameMode     the game mode
     * @param userName     the name of the current user
     */
    public LocalGameState(long timeInMillis, int maxPoints, int
            maxRounds, GameMode gameMode, String userName) {

        mGameMode = gameMode;
        if (gameMode == GameMode.TIME) {
            mGameTimeInMillis = timeInMillis;
            mCurrentTimeInMillis = 0;
        } else if (gameMode == GameMode.POINTS) {
            mMaxPoints = maxPoints;
        } else if (gameMode == GameMode.ROUNDS) {
            mMaxRounds = maxRounds;
            mCurrentRound = 0;
        }
        mUserPoints = 0;
        mAIPoints = 0;
        mIsUsersTurn = true;
        mUserName=userName;
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
