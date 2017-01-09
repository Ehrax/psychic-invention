package de.in.uulm.map.quartett.mainmenu;

import android.content.Intent;

import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

/**
 * Created by alex on 12/17/16.
 */

public interface MainMenuContract {

    interface Presenter extends BasePresenter {

        void startNewLocalGame();

        void startNewOnlineGame();

        void startAchievements();

        void startSettings();
    }

    interface View extends BaseView<Presenter> {

    }

    interface Backend {

        void startActivity(Intent intent);
    }
}
