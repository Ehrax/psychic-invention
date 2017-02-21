package de.in.uulm.map.quartett.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.ActivityUtils;

/**
 * Created by maxka on 08.01.2017.
 */

public class GameActivity extends DrawerActivity implements GameContract.BackEnd {

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
        mPresenter = new GamePresenter(gameFragment, getIntent().getExtras(),
                this, this);
        mPresenter.setIsStartingNewGame(!(getIntent().getExtras() == null));

        gameFragment.setPresenter(mPresenter);
    }

    @Override
    public void onBackPressed() {

        cancelRunningOperationsAndSaveGameState();
        super.onBackPressed();
    }

    @Override
    public void switchToView(GameContract.View view) {

        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                (Fragment) view, R.id.contentFrame);
        view.setPresenter(mPresenter);
        mPresenter.setView(view);
    }

    private void cancelRunningOperationsAndSaveGameState() {

        if (GameFragment.mCardLoader != null && !GameFragment.mCardLoader
                .isCancelled()) {
            GameFragment.mCardLoader.cancel(true);
        }
        if (mPresenter.mAI != null && !mPresenter.mAI.isCancelled()) {
            mPresenter.mAI.cancel(true);
        }
        if (GamePresenter.GameTimer != null) {
            GamePresenter.GameTimer.cancel();
        }
        if (mPresenter.getCurrentGameState() != null) {
            mPresenter.getCurrentGameState().save();
        }
    }

    @Override
    protected void onPause() {

        cancelRunningOperationsAndSaveGameState();
        super.onPause();
    }

    @Override
    public void startActivity(Intent intent, RoundWinner lastRoundWinner) {

        TransitionInflater inflater = TransitionInflater.from(this);
        if (lastRoundWinner != null) {
            getWindow().setEnterTransition(inflater.inflateTransition(R
                    .transition.fade_delay_750));
            if (lastRoundWinner == RoundWinner.USER) {
                getWindow().setExitTransition(inflater.inflateTransition(R.transition
                        .compare_user_wins_transition));
            } else if (lastRoundWinner == RoundWinner.AI) {
                getWindow().setExitTransition(inflater.inflateTransition(R.transition
                        .compare_ai_wins_transition));
            } else {
                getWindow().setExitTransition(inflater.inflateTransition(R.transition
                        .compare_draw_transition));
            }
        }

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this);
        super.startActivity(intent, options.toBundle());

        if (lastRoundWinner != null) {
            getWindow().setEnterTransition(inflater.inflateTransition(R
                    .transition.fade));
            getWindow().setExitTransition(inflater.inflateTransition(R.transition
                    .slide));
        }
    }
}
