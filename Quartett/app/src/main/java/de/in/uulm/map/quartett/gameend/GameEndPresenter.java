package de.in.uulm.map.quartett.gameend;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.LocalGameState;
import de.in.uulm.map.quartett.data.Statistic;
import de.in.uulm.map.quartett.game.GameActivity;
import de.in.uulm.map.quartett.game.RoundWinner;
import de.in.uulm.map.quartett.gamesettings.GameSettingsActivity;
import de.in.uulm.map.quartett.gamesettings.GameSettingsPresenter;
import de.in.uulm.map.quartett.mainmenu.MainMenuActivity;
import de.in.uulm.map.quartett.stats.stats.StatsPresenter;

import java.util.List;

/**
 * Created by jona on 1/10/17.
 */

public class GameEndPresenter implements GameEndContract.Presenter {

    private GameEndContract.View mView;

    private GameEndContract.Backend mBackend;

    private Intent mCallingIntent;

    private Context mContext;

    private LocalGameState mLastGameState;

    public static final String WINNER = "game-status";
    public static final String SUB = "game-sub-status";

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
                     Context context, LocalGameState lastGameState) {

        mView = view;
        mCallingIntent = callingIntent;
        mBackend = backend;
        mContext = context;
        mLastGameState = lastGameState;
    }

    /**
     * This is used to update the stats.
     */
    @Override
    public void start() {

        new AsyncStatUpdater().executeOnExecutor(AsyncTask
                .THREAD_POOL_EXECUTOR);

    }

    /**
     * This will be called when the initialization of the View has been
     * finished. It is used to assign the text given by the intent to the text
     * views of the view component.
     */
    @Override
    public void onViewCreated() {

        GameEndState gameEndState = (GameEndState)
                mCallingIntent.getSerializableExtra(WINNER);

        String gameSubStatus = mCallingIntent.getStringExtra(SUB);

        mView.setStatus(gameEndState == null ? GameEndState.DRAW : gameEndState);
        mView.setSubStatusText(gameSubStatus == null ? "" : gameSubStatus);
    }

    /**
     * This will be called when the restart Button has been clicked. The
     * function will restart the game with the currently set properties.
     */
    @Override
    public void onRestartClicked() {

        Intent intent = new Intent(mContext, GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra(GameSettingsPresenter.NAME, mLastGameState.mUserName);
        intent.putExtra(GameSettingsPresenter.MODE, mLastGameState.mGameMode);
        intent.putExtra(GameSettingsPresenter.LIMIT, mLastGameState.mLimit);
        intent.putExtra(GameSettingsPresenter.DECK, mLastGameState.mDeckID);

        LocalGameState.deleteAll(LocalGameState.class);
        mBackend.startActivity(intent);

    }

    /**
     * This will be called when the change settings Button has been clicked. The
     * function will jump to the game settings view, from where the game can be
     * restarted.
     */
    @Override
    public void onSettingsClicked() {

        LocalGameState.deleteAll(LocalGameState.class);
        Intent intent = new Intent(mContext, GameSettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mBackend.startActivity(intent);

    }

    /**
     * This will be called when the main menu Button has been clicked. The
     * function will then jump to the main menu.
     */
    @Override
    public void onMainMenuClicked() {

        LocalGameState.deleteAll(LocalGameState.class);
        Intent intent = new Intent(mContext, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mBackend.startActivity(intent);
    }

    /**
     * This class updates the games won, games lost and total games stats.
     */
    private class AsyncStatUpdater extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            String query = "SELECT * FROM Statistic WHERE m_Title = ?";

            List<Statistic> gamesWonList = Statistic.findWithQuery(Statistic.class,
                    query, StatsPresenter.GAME_WON);
            List<Statistic> gamesTotalList = Statistic.findWithQuery(Statistic
                            .class,
                    query, StatsPresenter.TOTAL_GAMES);
            List<Statistic> gamesLostList = Statistic.findWithQuery(Statistic.class,
                    query,
                    StatsPresenter.GAME_LOST);
            Statistic gamesWon, gamesLost, gamesTotal;

            gamesWon = gamesWonList.isEmpty() ? new Statistic(StatsPresenter
                    .GAME_WON, 0, mContext.getString(R.string
                    .stat_description_games_win)) : gamesWonList.get(0);
            gamesLost = gamesLostList.isEmpty() ? new Statistic(StatsPresenter
                    .GAME_LOST, 0, mContext.getString(R.string
                    .stat_description_games_lost)) : gamesLostList.get(0);
            gamesTotal = gamesTotalList.isEmpty() ? new Statistic(StatsPresenter
                    .TOTAL_GAMES, 0, mContext.getString(R.string
                    .stat_description_games_total)) : gamesTotalList.get(0);

            gamesTotal.mValue++;
            gamesTotal.save();

            GameEndState gameEndState = (GameEndState) mCallingIntent
                    .getSerializableExtra(WINNER);
            if (gameEndState == GameEndState.WIN) {
                gamesWon.mValue++;
            } else if (gameEndState == GameEndState.LOSE) {
                gamesLost.mValue++;
            }

            gamesLost.save();
            gamesWon.save();
            Log.d("Statistics: ", "gamesWon:" + gamesWon.toString());

            return null;
        }
    }
}
