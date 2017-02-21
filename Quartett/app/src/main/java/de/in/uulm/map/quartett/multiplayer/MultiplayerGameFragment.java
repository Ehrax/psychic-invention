package de.in.uulm.map.quartett.multiplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.gallery.CardFragment;

/**
 * Created by maxka on 02.02.2017.
 */

public class MultiplayerGameFragment extends Fragment implements
        MultiplayerContract.View {

    private MultiplayerContract.Presenter mPresenter;

    public static MultiplayerGameFragment newInstance() {

        return new MultiplayerGameFragment();
    }

    @Override
    public void setDelayedEnterTransition(boolean delayedEnterTransition) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_multiplayer_game,
                container, false);
        TurnData currentTurnData = mPresenter.getCurrentTurnData();
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id
                .card_frame_multiplayer);
        frameLayout.setTransitionGroup(true);
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.fade));
        setEnterTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.fade_delay));

        CardFragment cardFragment =mPresenter.createCurrentCardFragment();
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.add(R.id.card_frame_multiplayer, cardFragment);
        transaction.commit();

        TextView currentTurn = (TextView)view.findViewById(R.id
                .txt_multiplayer_turn_and_opponent_name);
        currentTurn.setText(getString(R.string.turn)+" "+currentTurnData
                .getCurrentTurn()
                +"/"+getString(R.string.max_multiplayer_turns)+" "+getString(R.string.vs)+" "+mPresenter.getOpponentName());

        TextView points = (TextView) view.findViewById(R.id
                .txt_multiplayer_points);
        points.setText(mPresenter.getThisPlayersPoints()+":"+mPresenter.getOtherPlayersPoints());


        return view;
    }

    @Override
    public void makeSnackBar(String message) {

    }

    @Override
    public void replaceProgressBar() {

    }

    @Override
    public void setPresenter(MultiplayerContract.Presenter presenter) {

        mPresenter = presenter;
    }
}
