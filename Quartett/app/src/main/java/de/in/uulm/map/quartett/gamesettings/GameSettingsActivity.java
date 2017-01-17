package de.in.uulm.map.quartett.gamesettings;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.ActivityUtils;

/**
 * Created by Jona on 08.01.2017.
 */

public class GameSettingsActivity extends DrawerActivity implements GameSettingsContract.Backend {

    /**
     * This will be called by the Android API. The function is used to attach
     * the GameSettingsFragment ot the GameSettingsActivity and make the
     * connections between View and Presenter.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        GameSettingsFragment gameSettingsFragment = (GameSettingsFragment)
                getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (gameSettingsFragment == null) {
            gameSettingsFragment = new GameSettingsFragment();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    gameSettingsFragment, R.id.contentFrame);
        }

        GameSettingsPresenter presenter =
                new GameSettingsPresenter(gameSettingsFragment, this, this);
        gameSettingsFragment.setPresenter(presenter);
    }

    /**
     * This method is used to go to the next Activity when all game settings are
     * entered.
     *
     * @param intent the intent to start the Activity
     */
    @Override
    public void nextActivity(Intent intent) {

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this);
        startActivity(intent,options.toBundle());
    }
}
