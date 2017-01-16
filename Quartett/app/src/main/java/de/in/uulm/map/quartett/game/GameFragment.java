package de.in.uulm.map.quartett.game;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.AutoTransition;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Image;
import de.in.uulm.map.quartett.gallery.CardFragment;
import de.in.uulm.map.quartett.gamesettings.GameMode;
import de.in.uulm.map.quartett.gamesettings.GameSettingsPresenter;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by maxka on 08.01.2017.
 */

public class GameFragment extends Fragment implements GameContract.View {

    private GameContract.Presenter mPresenter;
    /*
    Used to load the card fragment async
     */
    public static AsyncCardLoader mCardLoader;
    private CountDownLatch mCountDownLatchGame;

    private TextView mTxtLimit;

    public static GameFragment newInstance() {

        return new GameFragment();
    }

    @Override
    public void setPresenter(GameContract.Presenter presenter) {

        mCountDownLatchGame = new CountDownLatch(1);
        mPresenter = checkNotNull(presenter);

    }

    /**
     * This method should be used to create a Dialog that shows the description
     * of an Image from a Card.
     *
     * @param image the CardImage to take the description from
     */
    @Override
    public void showImageDescription(Image image) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.image_description_title);
        builder.setMessage(image.mDescription);
        builder.setPositiveButton("OK", null);

        builder.show();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game, container, false);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id
                .card_frame_in_game);
        frameLayout.setTransitionGroup(true);
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.fade));
        setEnterTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.fade_delay));

        mCardLoader = new AsyncCardLoader();
        mCardLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        ImageView imgViewTurn = (ImageView) view.findViewById(R.id.img_turn);
        imgViewTurn.setImageDrawable(mPresenter.getCurrentGameState()
                .mIsUsersTurn ? getActivity().getDrawable(R.drawable.ic_user) :
                getActivity().getDrawable(R.drawable.ic_ai));

        mTxtLimit = (TextView) view.findViewById(R.id
                .txt_in_game_limit);
        mTxtLimit.setText(mPresenter.getCurrentGameState().mGameMode ==
                GameMode.TIME ? getFormattedTime(mPresenter
                .getCurrentGameState().mCurrentTimeInMillis) : mPresenter.getCurrentGameState()
                .mGameMode == GameMode.POINTS ? "Point limit: " + mPresenter
                .getCurrentGameState().mLimit : mPresenter.getCurrentGameState()
                .mCurrentRound + "/" + mPresenter.getCurrentGameState()
                .mLimit);

        TextView txtViewPoints = (TextView) view.findViewById(R.id
                .txt_in_game_points);
        txtViewPoints.setText(mPresenter.getCurrentGameState().mUserPoints + " " +
                ": " + mPresenter.getCurrentGameState().mAIPoints);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id
                .progress_bar_ki_turn);

        progressBar.setVisibility(mPresenter.getCurrentGameState()
                .mIsUsersTurn ? View.INVISIBLE : View.VISIBLE);


        return view;
    }

    @Override
    public void updateGameTime(long timeInMillis) {

        mTxtLimit.setText(getFormattedTime(timeInMillis));
    }

    private String getFormattedTime(long timeInMillis) {

        long m, s;
        s = (timeInMillis / 1000) % 60;
        m = (timeInMillis / (1000 * 60)) % 60;

        return String.format(Locale.GERMAN, "%2d:%02d", m, s);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (mPresenter.getCurrentGameState() == null) {
            mPresenter.start();
        }
        mPresenter.startAI();

    }


    @Override
    public void onResume() {

        super.onResume();
        if (mPresenter.getCurrentGameState() == null) {
            mPresenter.start();
        }
        if (mPresenter.getCurrentGameState().mGameMode == GameMode.TIME) {
            if (GamePresenter.GameTimer != null) {
                GamePresenter.GameTimer.cancel();
            }
            mPresenter.restartGameTimer();
        }
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    /**
     * This AsyncTask is used to create the cardFragment. After the work is done
     * it removes the progressbar and shows the fragment.
     */
    public class AsyncCardLoader extends AsyncTask<Void, Void, CardFragment> {

        @Override
        protected CardFragment doInBackground(Void... params) {

            CardFragment cardFragment = mPresenter.getCurrentCardFragment();
            /*
            try {
                mCountDownLatchGame.await();
            }catch(InterruptedException e){

            }*/
            return isCancelled() ? null : cardFragment;
        }

        @Override
        protected void onPostExecute(CardFragment cardFragment) {

            if (cardFragment != null && !isCancelled()) {
                ViewGroup rootElement = (ViewGroup) getActivity()
                        .findViewById(R.id.lin_layout_game_root);

                ProgressBar progressBar = (ProgressBar) getActivity()
                        .findViewById(R.id.progress_bar_in_game);
                rootElement.removeView(progressBar);
                View placeHolder = getActivity().findViewById(R.id
                        .place_holder_view_in_game);
                rootElement.removeView(placeHolder);

                FragmentTransaction transaction = getFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.card_frame_in_game, cardFragment);
                transaction.commit();
            }
        }
    }
}
