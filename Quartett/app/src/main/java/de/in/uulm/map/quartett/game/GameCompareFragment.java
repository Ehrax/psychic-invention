package de.in.uulm.map.quartett.game;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Image;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by maxka on 08.01.2017.
 */

public class GameCompareFragment extends Fragment implements GameContract.View {

    private GameContract.Presenter mPresenter;

    private Attribute mComparedAttribute;
    private RoundWinner mWinner;

    @Override
    public void setPresenter(GameContract.Presenter presenter) {

        mPresenter = checkNotNull(presenter);
    }

    public void setAttribute(Attribute attr) {

        mComparedAttribute = attr;
    }

    public void setRoundWinner(RoundWinner winner) {

        mWinner = winner;
    }

    public static GameCompareFragment newInstance() {

        return new GameCompareFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game_compare,
                container, false);
        //Setting the Images
        ImageView imgUserCard = (ImageView) view.findViewById(R.id
                .img_user_card_compare);
        ImageView imgAICard = (ImageView) view.findViewById(R.id
                .img_ai_card_compare);
        imgUserCard.setImageDrawable(mPresenter.getCompareImage(true));
        imgAICard.setImageDrawable(mPresenter.getCompareImage(false));
        //Setting Attribute values
        TextView txtUserAttr = (TextView) view.findViewById(R.id
                .txt_user_atrribute_compare);
        TextView txtAIAttr = (TextView) view.findViewById(R.id
                .txt_ai_atrribute_compare);
        txtAIAttr.setText(mComparedAttribute.mName+": "+mPresenter
                .getCompareAttributeValue(false) + " " + mComparedAttribute.mUnit);
        txtUserAttr.setText(mComparedAttribute.mName+": "+mPresenter
                .getCompareAttributeValue(true) + " " + mComparedAttribute.mUnit);

        FrameLayout userFrame = (FrameLayout) view.findViewById(R.id
                .frame_lay_compare_bottom);
        FrameLayout aiFrame = (FrameLayout) view.findViewById(R.id
                .frame_lay_compare_top);

        //Setting the border red or green to indicate who lost and who won or
        // grey for draw
        userFrame.setBackground(mWinner == RoundWinner.USER ? getResources().getDrawable(R
                .drawable.compare_win_border) : mWinner == RoundWinner
                .AI ? getResources().getDrawable(R.drawable
                .compare_lost_border) : getResources().getDrawable(R.drawable
                .compare_draw_border));
        aiFrame.setBackground(mWinner == RoundWinner.USER ? getResources().getDrawable(R
                .drawable.compare_lost_border) : mWinner ==
                RoundWinner.AI ? getResources().getDrawable(R
                .drawable.compare_win_border) : getResources().getDrawable(R
                .drawable.compare_draw_border));
        //Setting the frame layout transition group to true because
        // ViewGroups usually are not animated by the transition framework
        userFrame.setTransitionGroup(true);
        aiFrame.setTransitionGroup(true);
        //setting the enter transition ai image slides in from top edge and
        // user image from bottom edge
        setEnterTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.slide_compare_game));
        //setting the exit transition
        if (mWinner == RoundWinner.USER) {
            setExitTransition(TransitionInflater.from(getContext())
                    .inflateTransition(R.transition.compare_user_wins_transition));
        } else if(mWinner == RoundWinner.AI) {
            setExitTransition(TransitionInflater.from(getContext())
                    .inflateTransition(R.transition
                            .compare_ai_wins_transition));
        } else{
            setExitTransition(TransitionInflater.from(getContext())
                    .inflateTransition(R.transition.compare_draw_transition));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPresenter.onClickCompare(mWinner);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        mPresenter.saveGameState();
        super.onPause();
    }

    @Override
    public void updateGameTime(long timeInMillis) {

    }

    @Override
    public void showImageDescription(Image image) {

    }
}
