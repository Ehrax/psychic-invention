package de.in.uulm.map.quartett.data;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by maxka on 08.01.2017. This holds the State of the current local
 * game.
 */

public class LocalGameState extends SugarRecord {

    public int mUserPoints;
    public int mAIPoints;
    public int mCurrentRound;
    public int mCurrentTimeInMillis;
    public long mGameTimeInMillis;
    public int mMaxPoints;
    public int mMaxRounds;
    public Highscore.HighScoreType mGameMode;

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
     */
    public LocalGameState(long timeInMillis, int maxPoints, int
            maxRounds, Highscore.HighScoreType gameMode) {

        mGameMode = gameMode;
        if (gameMode == Highscore.HighScoreType.TIME) {
            mGameTimeInMillis = timeInMillis;
            mCurrentTimeInMillis = 0;
        } else if (gameMode == Highscore.HighScoreType.POINT) {
            mMaxPoints = maxPoints;
        } else if (gameMode == Highscore.HighScoreType.ROUND) {
            mMaxRounds = maxRounds;
            mCurrentRound = 0;
        }
        mUserPoints = 0;
        mAIPoints = 0;
    }

    /**
     * Use this method to get a list of all Cards the User has in his deck.
     *
     * @return The users deck
     */
    public List<GameCard> getUserDeck() {

        return GameCard.find(GameCard.class, "m_game_state = ? and " +
                        "m_owner = ?",
                "" + this.getId(), "user");
    }

    /**
     * Use this method to get a list of all Cards the AI has in its deck.
     */
    public List<GameCard> getAIDeck() {

        return GameCard.find(GameCard.class, "m_game_state = ? and " +
                        "m_owner = ?",
                "" + this.getId(), "ai");
    }

}
