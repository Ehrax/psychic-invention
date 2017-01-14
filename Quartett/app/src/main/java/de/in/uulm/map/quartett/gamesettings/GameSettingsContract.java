package de.in.uulm.map.quartett.gamesettings;

import android.content.Intent;
import android.widget.Button;

import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

/**
 * Created by Jona on 08.01.2017.
 */

public interface GameSettingsContract {

    interface Presenter extends BasePresenter {

        void onNameChanged(String name, Button okButton);

        void onOkPressed();
    }

    interface View extends BaseView<Presenter> {

        String getName();

        int getPoints();

        long getTime();

        int getRounds();

        GameMode getMode();
    }

    interface Backend {

        void nextActivity(Intent intent);
    }
}
