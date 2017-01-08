package de.in.uulm.map.quartett.gamesetttings;

import android.widget.Button;

/**
 * Created by Jona on 08.01.2017.
 */

public class GameSettingsPresenter implements GameSettingsContract.Presenter {

    /**
     * This stores the currently entered name.
     */
    public String mName;

    /**
     * This stores the currently entered point limit.
     */
    public int mPoints;

    /**
     * This stores the currently entered time limit in minutes.
     */
    public int mTime;

    /**
     * This stores the currently entered game mode.
     */
    public GameSettingsActivity.GameMode mMode;

    @Override
    public void start() {

    }

    /**
     * This method will be triggered whenever the name is changed. The okButton
     * is also passed to the method as the state of this button depends on
     * whether name has be entered or not.
     */
    @Override
    public void onNameChanged(String name, Button okButton) {

        if (name != null && name.length() > 0) {
            okButton.setEnabled(true);
        }

        mName = name;
    }

    /**
     * This method will be called whenever the points are changed.
     */
    @Override
    public void onPointsChanged(int points) {

        mPoints = points;
    }

    /**
     * This method will be called whenever the time has changed.
     */
    @Override
    public void onTimeChanged(int time) {

        mTime = time;
    }

    /**
     * This method will be called whenever the game mode has changed.
     */
    @Override
    public void onModeChanged(GameSettingsActivity.GameMode mode) {

        mMode = mode;
    }

    /**
     * This method  will be called whenever the ok button is pressed.
     */
    @Override
    public void onOkPressed() {

    }
}
