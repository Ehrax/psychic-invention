package de.in.uulm.map.quartett.multiplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.game.GameFragment;
import de.in.uulm.map.quartett.game.GamePresenter;
import de.in.uulm.map.quartett.util.ActivityUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

/**
 * Created by maxka on 29.01.2017.
 */

public class MultiplayerActivity extends DrawerActivity implements
        MultiplayerContract.Backend{

    private MultiplayerPresenter mPresenter;

    private CurrentGamesAdapter mAdapter;

    private static int RC_SIGN_IN = 9001;
    private static int RC_SELECT_PLAYERS = 9002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mAdapter = new CurrentGamesAdapter(this);

        MultiplayerFragment multiplayerFragment = (MultiplayerFragment)
                getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (multiplayerFragment == null) {
            multiplayerFragment = MultiplayerFragment.newInstance();
            multiplayerFragment.setAdapter(mAdapter);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    multiplayerFragment, R.id.contentFrame);
        }
        mPresenter = new MultiplayerPresenter(multiplayerFragment,this,
                mAdapter,this);
        mAdapter.setPresenter(mPresenter);
        multiplayerFragment.setPresenter(mPresenter);

        mPresenter.start();
    }

    @Override
    public boolean resolveConnectionFailure(GoogleApiClient client,
                                      ConnectionResult result) {

        return BaseGameUtils.resolveConnectionFailure(this,
                client, result,
                RC_SIGN_IN, getString(R.string.signin_other_error));
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mPresenter.onResolveConnectionFailureResult(resultCode,requestCode);
        }else if(requestCode == RC_SELECT_PLAYERS){
            if(resultCode != RESULT_OK){
                return;
            }
            mPresenter.createMatch(intent);
        }
    }

    @Override
    public void showActivityResultError(int requestCode, int resultCode) {
        BaseGameUtils.showActivityResultError(this,
                requestCode, resultCode, R.string.signin_failure);
    }

    @Override
    public void startSelectOpponent(GoogleApiClient client) {
        Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent
                (client, 1, 1, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    @Override
    protected void onStart() {

        super.onStart();
        mPresenter.connectToGoogleGames();
    }

    @Override
    protected void onStop() {

        super.onStop();
        mPresenter.disconnectFromGoogleGames();
    }

    @Override
    public void switchToView(MultiplayerContract.View view) {

        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                (Fragment) view, R.id.contentFrame);
        view.setPresenter(mPresenter);
        mPresenter.setView(view);
    }

    @Override
    public MultiplayerActivity getActivity() {

        return this;
    }
}
