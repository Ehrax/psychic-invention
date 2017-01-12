package de.in.uulm.map.quartett.stats.stats;

import android.content.Context;

import de.in.uulm.map.quartett.data.Achievement;
import de.in.uulm.map.quartett.data.Statistic;
import de.in.uulm.map.quartett.stats.StatsContract;

import java.util.List;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class StatsPresenter implements StatsContract.StatsPresenter {

    /**
     * Reference to view
     */
    StatsContract.StatsView mView;

    /**
     * Context needed for intent construction.
     */
    Context mContext;

    /**
     * all data which is needed in future process for setting views
     */
    private List<Statistic> mGamesWon;
    private List<Statistic> mGamesLost;
    private List<Statistic> mHandsWon;
    private List<Statistic> mHandsLost;
    private List<Achievement> mAchievements;

    /**
     * use this static variables to access the correct mTitles in the Database
     */
    public static final String GAME_WON = "game_won";
    public static final String GAME_LOST = "game_lost";
    public static final String HAND_WON = "hand_won";
    public static final String HAND_LOST = "hand_lost";


    public StatsPresenter(StatsContract.StatsView view, Context ctx) {

        this.mView = view;
        this.mContext = ctx;
    }

    /**
     * this method is loading data from the database to later on set the
     * StatsFragment View to the specific statistics
     */
    @Override
    public void start() {

        String query =  "SELECT * FROM Statistic, WHERE mTitle = ?";

        mGamesWon = Statistic.findWithQuery(Statistic.class, query , GAME_WON);
        mGamesLost = Statistic.findWithQuery(Statistic.class, query, GAME_LOST);
        mHandsWon = Statistic.findWithQuery(Statistic.class, query, HAND_WON);
        mHandsLost = Statistic.findWithQuery(Statistic.class, query, HAND_LOST);
        mAchievements = Achievement.listAll(Achievement.class);
    }
}
