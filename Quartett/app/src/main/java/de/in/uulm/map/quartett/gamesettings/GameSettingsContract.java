package de.in.uulm.map.quartett.gamesettings;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

/**
 * Created by Jona on 08.01.2017.
 */

public interface GameSettingsContract {

    interface Presenter extends BasePresenter {

        void onNameChanged(String name, Button okButton);

        void onOkPressed();

        void setGameMode(RadioGroup radioGroup, String mode);

        void setUserName(EditText editText, String name);
    }

    interface View extends BaseView<Presenter> {

        String getName();

        long getLimit();

        GameMode getMode();

        GameLevel getLevel();
    }

    interface Backend {

        void nextActivity(Intent intent);
    }
}
