package de.in.uulm.map.quartett.stats;

import android.content.Intent;

import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public interface StatsContract {

    interface StatsPresenter extends BasePresenter {}

    interface AchievementsPresenter extends BasePresenter {}

    interface RankingPresenter extends BasePresenter{}

    interface StatsView extends BaseView<StatsPresenter> {}

    interface AchievementsView extends BaseView<AchievementsPresenter> {}

    interface RankingView extends BaseView<RankingPresenter> {}

    interface Backend {

        void nextActivity(Intent intent);
    }

}
