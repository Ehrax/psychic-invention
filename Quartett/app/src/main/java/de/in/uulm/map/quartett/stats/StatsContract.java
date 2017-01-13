package de.in.uulm.map.quartett.stats;

import android.widget.TextView;


import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;
import de.in.uulm.map.quartett.views.BetterArcProgress;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public interface StatsContract {

    // Stats view and presenter
    interface StatsView extends BaseView<StatsPresenter> {
        void fragmentBecomeVisible();
    }

    interface StatsPresenter extends BasePresenter {

        void setArcProgressWin(BetterArcProgress arcProgress);

        void setArcProgressAchiev(BetterArcProgress arcProgress);

        void setArcProgressHands(BetterArcProgress arcProgress);

        void setTextGamesWon(TextView textView);

        void setTextGamesLost(TextView textView);

        void setTextHandsWon(TextView textView);

        void setTextHandsLost(TextView textView);
    }


    interface AchievementsPresenter extends BasePresenter {}

    interface AchievementsView extends BaseView<AchievementsPresenter> {
        void fragmentBecomeVisible();
    }


    interface RankingPresenter extends BasePresenter {}

    interface RankingView extends BaseView<RankingPresenter> {
        void fragmentBecomeVisible();
    }


}
