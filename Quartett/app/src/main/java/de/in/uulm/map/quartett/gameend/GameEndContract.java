package de.in.uulm.map.quartett.gameend;

import android.content.Intent;

import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

/**
 * Created by jona on 1/10/17.
 */

public interface GameEndContract {

    interface Presenter extends BasePresenter {

        void onViewCreated();

        void onRestartClicked();

        void onSettingsClicked();

        void onMainMenuClicked();
    }

    interface View extends BaseView<Presenter> {

        void setStatus(GameEndState endState);

        void setSubStatusText(String text);
    }

    interface Backend {

        void startActivity(Intent intent);
    }
}
