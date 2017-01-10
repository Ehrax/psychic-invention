package de.in.uulm.map.quartett.gameend;

import android.content.Context;
import android.content.Intent;

import de.in.uulm.map.quartett.mainmenu.MainMenuActivity;

/**
 * Created by jona on 1/10/17.
 */

public class GameEndPresenter implements GameEndContract.Presenter {

    private GameEndContract.View mView;

    private GameEndContract.Backend mBackend;

    private Intent mCallingIntent;

    private Context mContext;

    GameEndPresenter(GameEndContract.View view,
                     GameEndContract.Backend backend,
                     Intent callingIntent,
                     Context context) {

        mView = view;
        mCallingIntent = callingIntent;
        mBackend = backend;
        mContext = context;
    }

    @Override
    public void start() {

    }

    @Override
    public void onViewCreated() {

        mView.setStatusText(mCallingIntent.getStringExtra("game-status"));
        mView.setStatusText(mCallingIntent.getStringExtra("game-sub-status"));
    }

    @Override
    public void onRestartClicked() {

        // TODO: add game loading activity
    }

    @Override
    public void onSettingsClicked() {

        // TODO: add settings activity
    }

    @Override
    public void onMainMenuClicked() {

        Intent intent = new Intent(mContext, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mBackend.startActivity(intent);
    }
}
