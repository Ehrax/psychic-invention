package de.in.uulm.map.quartett.mainmenu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import de.in.uulm.map.quartett.data.Deck;

/**
 * Created by alex on 12/17/16.
 */

public class MainMenuPresenter implements MainMenuContract.Presenter {

    @NonNull
    private final MainMenuContract.View mView;
    private final Context ctx;

    public MainMenuPresenter(@NonNull MainMenuContract.View mainMenuView,
                             Context ctx) {

        mView = mainMenuView;
        this.ctx = ctx;
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
