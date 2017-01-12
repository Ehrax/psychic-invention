package de.in.uulm.map.quartett.stats.achievements;

import android.content.Context;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import de.in.uulm.map.quartett.stats.StatsContract;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class AchievementsPresenter implements StatsContract.AchievementsPresenter {

    /**
     * Reference to the view
     */
    StatsContract.AchievementsView mView;

    /**
     * Context needed for intent construction
     */
    Context mContext;

    public AchievementsPresenter(StatsContract.AchievementsView mView, Context
            mContext) {

        this.mView = mView;
        this.mContext = mContext;
    }

    @Override
    public void start(ArcProgress arcProgressWin, ArcProgress arcProgressAchiev, ArcProgress arcProgressHands, TextView textViewsHandsWon, TextView textViewGamesLost, TextView viewsHandsWon, TextView textViewHandsLost) {

    }
}
