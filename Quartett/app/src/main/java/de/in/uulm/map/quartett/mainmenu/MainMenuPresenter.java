package de.in.uulm.map.quartett.mainmenu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;

import de.in.uulm.map.quartett.game.GameActivity;

/**
 * Created by alex on 12/17/16.
 */

public class MainMenuPresenter implements MainMenuContract.Presenter {

    @NonNull
    private final MainMenuContract.View mView;
    private final Context mCtx;

    public MainMenuPresenter(@NonNull MainMenuContract.View mainMenuView,
                             Context ctx) {

        mView = mainMenuView;
        this.mCtx = ctx;
        start();
    }

    @Override
    public void start() {

    }

    /**
     * Starts the local game activity
     */
    @Override
    public void startNewLocalGame() {
        //TODO: start local game activity
        Intent intent = new Intent(mCtx, GameActivity.class);
        mCtx.startActivity(intent);
    }

    /**
     * starts the multiplayer activity
     */
    @Override
    public void startNewOnlineGame() {
        //TODO: start online game activity

    }

    /**
     * starts the achievement activity
     */
    @Override
    public void startAchievements() {
        //TODO: start achievement activity

    }

    /**
     * starts the settings activity
     */
    @Override
    public void startSettings() {
        //TODO: start settings activity

    }
}
