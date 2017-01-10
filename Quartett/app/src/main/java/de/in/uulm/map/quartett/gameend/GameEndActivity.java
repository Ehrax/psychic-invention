package de.in.uulm.map.quartett.gameend;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.mainmenu.MainMenuFragment;
import de.in.uulm.map.quartett.util.ActivityUtils;

/**
 * Created by jona on 1/10/17.
 */

public class GameEndActivity extends DrawerActivity implements GameEndContract.Backend{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        GameEndFragment gameEndFragment;

        Fragment fragment = getFragmentManager().
                findFragmentById(R.id.contentFrame);

        if (fragment instanceof GameEndFragment) {
            gameEndFragment = (GameEndFragment) fragment;
        } else {
            gameEndFragment = new GameEndFragment();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    gameEndFragment, R.id.contentFrame);
        }

        GameEndContract.Presenter presenter =
                new GameEndPresenter(gameEndFragment, this, this);
        gameEndFragment.setPresenter(presenter);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void startActivity(Intent intent) {

        super.startActivity(intent);
    }
}
