package de.in.uulm.map.quartett.gamesetttings;

import android.widget.Button;

import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

/**
 * Created by Jona on 08.01.2017.
 */

public interface GameSettingsContract {

    interface Presenter extends BasePresenter {

        void onNameChanged(String name, Button okButton);

        void onPointsChanged(int points);

        void onTimeChanged(int time);

        void onModeChanged(GameSettingsActivity.GameMode mode);

        void onOkPressed();
    }

    interface View extends BaseView<Presenter> {

    }
}
