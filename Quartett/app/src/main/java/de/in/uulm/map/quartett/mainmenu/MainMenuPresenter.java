package de.in.uulm.map.quartett.mainmenu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.orm.dsl.NotNull;

import de.in.uulm.map.quartett.gamesettings.GameSettingsActivity;

/**
 * Created by alex on 12/17/16.
 */

public class MainMenuPresenter implements MainMenuContract.Presenter {

    @NonNull
    private final MainMenuContract.View mView;

    @NotNull
    private final MainMenuContract.Backend mBackend;

    @NotNull
    private final Context ctx;

    public MainMenuPresenter(@NonNull MainMenuContract.View mainMenuView,
                             MainMenuContract.Backend backend,
                             Context ctx) {

        mView = mainMenuView;
        mBackend = backend;
        this.ctx = ctx;
        start(arcProgressWin, arcProgressAchiev, arcProgressHands, textViewsHandsWon, textViewGamesLost, textViewsHandsWon, textViewHandsLost);
    }

    @Override
    public void start(ArcProgress arcProgressWin, ArcProgress arcProgressAchiev, ArcProgress arcProgressHands, TextView textViewsHandsWon, TextView textViewGamesLost, TextView viewsHandsWon, TextView textViewHandsLost) {

    }

    /**
     * Starts a new local game by calling the GameSettingsActivity.
     */
    @Override
    public void startNewLocalGame() {

        Intent intent = new Intent(ctx, GameSettingsActivity.class);
        mBackend.startActivity(intent);
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
