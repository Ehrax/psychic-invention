package de.in.uulm.map.quartett.gamesetttings;

import android.app.Fragment;
import android.os.Bundle;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.ActivityUtils;

/**
 * Created by Jona on 08.01.2017.
 */

public class GameSettingsActivity extends DrawerActivity {

    /**
     * This enum is used to store the game mode in a type safe way.
     */
    public enum GameMode {

        POINTS,
        TIME,
        INSANE
    }

    /**
     * This will be called by the Android API. The function is used to attach
     * the GameSettingsFragment ot the GameSettingsActivity and make the
     * connections between View and Presenter.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        GameSettingsFragment gameSettingsFragment;

        Fragment fragment = getFragmentManager().
                findFragmentById(R.id.contentFrame);

        if (fragment instanceof GameSettingsFragment) {
            gameSettingsFragment = (GameSettingsFragment) fragment;
        } else {
            gameSettingsFragment = new GameSettingsFragment();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    gameSettingsFragment, R.id.contentFrame);
        }

        GameSettingsPresenter presenter = new GameSettingsPresenter();
        gameSettingsFragment.setPresenter(presenter);
    }
}
