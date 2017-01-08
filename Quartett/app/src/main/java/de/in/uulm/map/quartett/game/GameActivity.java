package de.in.uulm.map.quartett.game;

import android.os.Bundle;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.ActivityUtils;

/**
 * Created by maxka on 08.01.2017.
 */

public class GameActivity extends DrawerActivity {
    private GamePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        GameFragment gameFragment = (GameFragment) getSupportFragmentManager
                ().findFragmentById(R.id.contentFrame);
        if(gameFragment == null){
            gameFragment = GameFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    gameFragment,R.id.contentFrame);
        }

        mPresenter = new GamePresenter(gameFragment,this);
        gameFragment.setPresenter(mPresenter);
    }

    @Override
    public void onBackPressed() {
        if(GameFragment.mCardLoader != null && !GameFragment.mCardLoader
                .isCancelled()) {
            GameFragment.mCardLoader.cancel(true);
        }
        super.onBackPressed();
    }
}
