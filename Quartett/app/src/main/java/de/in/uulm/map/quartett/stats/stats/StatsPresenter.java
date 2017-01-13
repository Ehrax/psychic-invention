package de.in.uulm.map.quartett.stats.stats;

import android.content.Context;
import android.widget.TextView;


import de.in.uulm.map.quartett.data.Achievement;
import de.in.uulm.map.quartett.data.Statistic;
import de.in.uulm.map.quartett.stats.StatsContract;
import de.in.uulm.map.quartett.views.BetterArcProgress;

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
    private List<Statistic> mGamesTotal;
    private List<Achievement> doneAchievements;
    private List<Achievement> undoneAchievements;

    /**
     * use this static variables to access the correct mTitles in the Database
     */
    public static final String GAME_WON = "game_won";
    public static final String GAME_LOST = "game_lost";
    public static final String HAND_WON = "hand_won";
    public static final String HAND_LOST = "hand_lost";
    public static final String TOTAL_GAMES = "total_games";

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

        String query = "SELECT * FROM Statistic WHERE m_Title = ?";

        mGamesWon = Statistic.findWithQuery(Statistic.class, query, GAME_WON);
        mGamesLost = Statistic.findWithQuery(Statistic.class, query, GAME_LOST);
        mGamesTotal = Statistic.findWithQuery(Statistic.class, query, TOTAL_GAMES);
        mHandsWon = Statistic.findWithQuery(Statistic.class, query, HAND_WON);
        mHandsLost = Statistic.findWithQuery(Statistic.class, query, HAND_LOST);

        doneAchievements = Achievement.findWithQuery(Achievement.class,
                "SELECT * FROM Achievement WHERE m_Value==m_Target_Value");

        undoneAchievements = Achievement.findWithQuery(Achievement.class,
                "SELECT * FROM Achievement WHERE m_Value!=m_Target_Value");
    }

    /**
     * This method is calculating the win percentage, after the calculations are
     * done this method is setting the ArcProgress view which shows the won %
     * percentage
     *
     * @param arcProgress id/stats_win_circle
     */
    @Override
    public void setArcProgressWin(BetterArcProgress arcProgress) {

        if (mGamesWon.size() == 0 || mGamesTotal.size() == 0) {
            arcProgress.setProgress(0);
        } else {
            Statistic gamesWon = mGamesWon.get(0);
            Statistic gamesTotal = mGamesLost.get(0);

            int win = (int) gamesWon.mValue;
            int total = (int) gamesTotal.mValue;

            int winPercentage = win / total * 100;

            arcProgress.setProgress(winPercentage);
        }
    }

    /**
     * This method is calculating the achievement progress percentage, when the
     * calculations are done this method is setting the ArcProgress view which
     * shows the achievement percentage
     *
     * @param arcProgress id/stats_achievement_circle
     */
    @Override
    public void setArcProgressAchiev(BetterArcProgress arcProgress) {

        if (doneAchievements.size() == 0) {
            arcProgress.setProgress(0);

        } else {
            int doneAchiev = doneAchievements.size();
            int undoneAchiev = undoneAchievements.size();

            // TODO
            float achievPercentage = undoneAchiev / doneAchiev;

            arcProgress.setProgress((int) achievPercentage);
        }
    }

    /**
     * This method is calculating the hands won hands lost statistic, when th?
     * calculations are done this method is setting the ArcProgress view which
     * shows the hands, won hands lost statistic
     *
     * @param arcProgress id/stats_hands_circle
     */
    @Override
    public void setArcProgressHands(BetterArcProgress arcProgress) {

        if (mHandsWon.size() == 0 || mHandsLost.size() == 0) {
            arcProgress.setProgress(0);
            arcProgress.setSuffixText(" ");
        } else {
            Statistic handsWon = mHandsWon.get(0);
            Statistic handsLost = mHandsLost.get(0);

            int win = (int) handsWon.mValue;
            int lost = (int) handsLost.mValue;

            float killDeathRatio = win/lost;


            // TODO
            String rest = String.format(java.util.Locale.US, "%.1f",
                    killDeathRatio % 1);

            arcProgress.setProgress((int) killDeathRatio);
            arcProgress.setSuffixText(String.valueOf(rest));
        }
    }

    /**
     * Sets the TextView to total amount of games won
     *
     * @param textView id/stats_games_won
     */
    @Override
    public void setTextGamesWon(TextView textView) {

        if (mHandsWon.size() == 0) {
            textView.setText("0");
        } else {
            Statistic gamesWon = mGamesWon.get(0);
            int won = (int) gamesWon.mValue;

            textView.setText(String.valueOf(won));
        }
    }

    /**
     * Sets the TextView to total amount of games lost
     *
     * @param textView id/stats_games_lost
     */
    @Override
    public void setTextGamesLost(TextView textView) {

        if (mGamesLost.size() == 0) {
            textView.setText("0");
        } else {
            Statistic gameLost = mGamesLost.get(0);
            int lost = (int) gameLost.mValue;

            textView.setText(String.valueOf(lost));
        }
    }

    /**
     * Sets the TextView to total amount of hands won
     *
     * @param textView id/stats_hands_won
     */
    @Override
    public void setTextHandsWon(TextView textView) {

        if (mHandsWon.size() == 0) {
            textView.setText("0");
        } else {
            Statistic handWon = mHandsWon.get(0);
            int won = (int) handWon.mValue;

            textView.setText(String.valueOf(won));
        }
    }

    /**
     * Sets the TextView total to total amount of hands lost
     */
    @Override
    public void setTextHandsLost(TextView textView) {

        if (mHandsLost.size() == 0) {
            textView.setText("0");
        } else {
            Statistic handsLost = mHandsLost.get(0);
            int lost = (int) handsLost.mValue;

            textView.setText(String.valueOf(lost));
        }

    }

}
