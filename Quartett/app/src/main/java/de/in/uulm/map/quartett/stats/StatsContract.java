package de.in.uulm.map.quartett.stats;

import android.content.Intent;

import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public interface StatsContract {

    // Stats view and presenter
    interface StatsView extends BaseView<StatsPresenter> {}

    interface StatsPresenter extends BasePresenter {}



    interface AchievementsPresenter extends BasePresenter {}

    interface AchievementsView extends BaseView<AchievementsPresenter> {}


    interface RankingPresenter extends BasePresenter{}

    interface RankingView extends BaseView<RankingPresenter> {}


}
