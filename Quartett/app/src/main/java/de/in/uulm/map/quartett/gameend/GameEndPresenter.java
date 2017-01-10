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

    /**
     * Simple constructor to initialize member variables.
     *
     * @param view          the view component this presenter is connected to
     * @param backend       the Backend
     * @param callingIntent the Intent this activity was started with
     * @param context       the Context of the Activity
     */
    GameEndPresenter(GameEndContract.View view,
                     GameEndContract.Backend backend,
                     Intent callingIntent,
                     Context context) {

        mView = view;
        mCallingIntent = callingIntent;
        mBackend = backend;
        mContext = context;
    }

    /**
     * This could be used for some extra initialization ...
     */
    @Override
    public void start() {

        // ... nothing to do here.
    }

    /**
     * This will be called when the initialization of the View has been
     * finished. It is used to assign the text given by the intent to the text
     * views of the view component.
     */
    @Override
    public void onViewCreated() {

        GameEndState gameEndState = (GameEndState)
                mCallingIntent.getSerializableExtra("game-status");

        String gameSubStatus = mCallingIntent.getStringExtra("game-sub-status");

        mView.setStatus(gameEndState == null ? GameEndState.DRAW : gameEndState);
        mView.setSubStatusText(gameSubStatus == null ? "" : gameSubStatus);
    }

    /**
     * This will be called when the restart Button has been clicked. The
     * function will restart the game with the currently set properties.
     */
    @Override
    public void onRestartClicked() {

        // TODO: add game loading activity
    }

    /**
     * This will be called when the change settings Button has been clicked.
     * The function will jump to the game settings view, from where the game
     * can be restarted.
     */
    @Override
    public void onSettingsClicked() {

        // TODO: add settings activity
    }

    /**
     * This will be called when the main menu Button has been clicked.
     * The function will then jump to the main menu.
     */
    @Override
    public void onMainMenuClicked() {

        Intent intent = new Intent(mContext, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mBackend.startActivity(intent);
    }
}
