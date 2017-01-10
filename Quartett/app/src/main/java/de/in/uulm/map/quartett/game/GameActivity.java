package de.in.uulm.map.quartett.game;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.gallery.GalleryContract;
import de.in.uulm.map.quartett.util.ActivityUtils;

/**
 * Created by maxka on 08.01.2017.
 */

public class GameActivity extends DrawerActivity implements GameContract
        .BackEnd {

    private GamePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        GameFragment gameFragment = (GameFragment) getSupportFragmentManager
                ().findFragmentById(R.id.contentFrame);
        if (gameFragment == null) {
            gameFragment = GameFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    gameFragment, R.id.contentFrame);
        }

        mPresenter = new GamePresenter(gameFragment, this, this);
        gameFragment.setPresenter(mPresenter);
    }

    @Override
    public void onBackPressed() {

        if (GameFragment.mCardLoader != null && !GameFragment.mCardLoader
                .isCancelled()) {
            GameFragment.mCardLoader.cancel(true);
        }
        if(mPresenter.mAI != null && !mPresenter.mAI.isCancelled()){
            mPresenter.mAI.cancel(true);
        }
        super.onBackPressed();
    }

    @Override
    public void switchToView(GameContract.View view) {

        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                (Fragment) view, R.id.contentFrame);
        view.setPresenter(mPresenter);
    }


}
