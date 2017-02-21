package de.in.uulm.map.quartett.multiplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.game.RoundWinner;

/**
 * Created by maxka on 03.02.2017.
 */

public class MultiplayerGameCompareFragment extends Fragment implements
        MultiplayerContract.View {

    private MultiplayerContract.Presenter mPresenter;

    private boolean needDelayedEnterTransition = false;

    public static MultiplayerGameCompareFragment newInstance() {

        return new MultiplayerGameCompareFragment();
    }

    @Override
    public void setDelayedEnterTransition(boolean delayedEnterTransition) {

        needDelayedEnterTransition = delayedEnterTransition;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout
                .fragment_game_compare_multiplayer, container, false);

        ImageView thisPlayerCompareImage = (ImageView) view.findViewById(R.id
                .img_user_card_compare_mp);
        ImageView otherPlayerCompareImage = (ImageView) view.findViewById(R
                .id.img_opponent_card_compare);

        mPresenter.setCompareImage(thisPlayerCompareImage, true);
        mPresenter.setCompareImage(otherPlayerCompareImage, false);

        TextView txtThisPlayerAttr = (TextView) view.findViewById(R.id
                .txt_user_atrribute_compare_mp);
        TextView txtOtherPlayerAttr = (TextView) view.findViewById(R.id
                .txt_opponent_atrribute_compare);

        txtThisPlayerAttr.setText(mPresenter.getCurrentAttribute().mName + ": " +
                "" + mPresenter.getPlayerCompareAttributeValue(true).mValue + " " + mPresenter
                .getCurrentAttribute().mUnit);
        txtOtherPlayerAttr.setText(mPresenter.getCurrentAttribute().mName + ": " +
                "" + mPresenter.getPlayerCompareAttributeValue(false).mValue + " " + mPresenter
                .getCurrentAttribute().mUnit);
        FrameLayout thisPlayerFrame = (FrameLayout) view.findViewById(R.id
                .frame_lay_compare_bottom_mp);
        FrameLayout otherPlayerFrame = (FrameLayout) view.findViewById(R.id
                .frame_lay_compare_top_mp);

        if (mPresenter.isThisPlayerFirstPlayer()) {
            thisPlayerFrame.setBackground(mPresenter
                    .getCurrentTurnData().getCurrentTurnWinner
                            () == MultiplayerTurnWinner.FIRST ?
                    getResources().getDrawable(R.drawable.compare_win_border) :
                    mPresenter.getCurrentTurnData().getCurrentTurnWinner
                            () == MultiplayerTurnWinner.SECOND ? getResources().getDrawable(R
                            .drawable.compare_lost_border) : getResources().getDrawable(R.drawable
                            .compare_draw_border));
            otherPlayerFrame.setBackground(mPresenter
                    .getCurrentTurnData().getCurrentTurnWinner
                            () == MultiplayerTurnWinner.FIRST ?
                    getResources().getDrawable(R.drawable.compare_lost_border) :
                    mPresenter.getCurrentTurnData().getCurrentTurnWinner
                            () == MultiplayerTurnWinner.SECOND ?
                            getResources().getDrawable(R.drawable.compare_win_border)
                            : getResources().getDrawable(R.drawable.compare_draw_border));
        } else {
            thisPlayerFrame.setBackground(mPresenter.getCurrentTurnData().getCurrentTurnWinner
                    () ==
                    MultiplayerTurnWinner.SECOND ?
                    getResources().getDrawable(R.drawable.compare_win_border) :
                    mPresenter.getCurrentTurnData().getCurrentTurnWinner
                            () == MultiplayerTurnWinner.FIRST ?
                            getResources().getDrawable(R
                                    .drawable.compare_lost_border) : getResources().getDrawable(R.drawable
                            .compare_draw_border));
            otherPlayerFrame.setBackground(mPresenter.getCurrentTurnData().getCurrentTurnWinner
                    () ==
                    MultiplayerTurnWinner.SECOND ?
                    getResources().getDrawable(R.drawable.compare_lost_border) :
                    mPresenter.getCurrentTurnData().getCurrentTurnWinner
                            () == MultiplayerTurnWinner.FIRST ?
                            getResources().getDrawable(R.drawable.compare_win_border)
                            : getResources().getDrawable(R.drawable.compare_draw_border));
        }

        thisPlayerFrame.setTransitionGroup(true);
        otherPlayerFrame.setTransitionGroup(true);

        setEnterTransition();


        if ((mPresenter
                .getCurrentTurnData().getCurrentTurnWinner
                        () == MultiplayerTurnWinner.FIRST &&
                mPresenter.isThisPlayerFirstPlayer()) || (mPresenter
                .getCurrentTurnData().getCurrentTurnWinner
                        () == MultiplayerTurnWinner.SECOND && !mPresenter
                .isThisPlayerFirstPlayer())) {
            setExitTransition(TransitionInflater.from(getContext())
                    .inflateTransition(R.transition
                            .compare_user_wins_transition_mp));
        } else if ((mPresenter
                .getCurrentTurnData().getCurrentTurnWinner
                        () == MultiplayerTurnWinner.FIRST &&
                !mPresenter.isThisPlayerFirstPlayer()) || (mPresenter
                .getCurrentTurnData().getCurrentTurnWinner
                        () == MultiplayerTurnWinner.SECOND && mPresenter
                .isThisPlayerFirstPlayer())) {
            setExitTransition(TransitionInflater.from(getContext())
                    .inflateTransition(R.transition
                            .compare_opponent_wins_transition));
        } else {
            setExitTransition(TransitionInflater.from(getContext())
                    .inflateTransition(R.transition
                            .compare_draw_transition_mp));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPresenter.onClickCompare();
            }
        });
        mPresenter.setSawCompare(true);
        return view;
    }

    private void setEnterTransition() {

        if (needDelayedEnterTransition) {
            setEnterTransition(TransitionInflater.from(getContext())
                    .inflateTransition(R.transition.slide_compare_mp_delayed));
        } else {
            setEnterTransition(TransitionInflater.from(getContext())
                    .inflateTransition(R.transition.slide_compare_mp));
        }
    }

    @Override
    public void makeSnackBar(String message) {

        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void replaceProgressBar() {

    }

    @Override
    public void setPresenter(MultiplayerContract.Presenter presenter) {

        mPresenter = presenter;
    }
}
