package de.in.uulm.map.quartett.game;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.gallery.CardFragment;

/**
 * Created by maxka on 08.01.2017.
 */

public class GameFragment extends Fragment implements GameContract.View {

    private GameContract.Presenter mPresenter;
    /*
    Used to load the card fragment async
     */
    public static AsyncCardLoader mCardLoader;

    public static GameFragment newInstance() {

        return new GameFragment();
    }

    @Override
    public void setPresenter(GameContract.Presenter presenter) {

        mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game, container, false);
        mCardLoader = new AsyncCardLoader();
        mCardLoader.execute();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mPresenter.start();
    }

    @Override
    public void onResume() {

        super.onResume();
        if (mPresenter.getCurrentGameState() == null) {
            mPresenter.start();
        }
    }

    @Override
    public void onPause() {

        mPresenter.saveGameState();
        super.onPause();
    }

    /**
     * This AsyncTask is used to create the cardFragment. After the work is
     * done it removes the progressbar and shows the fragment.
     */
    public class AsyncCardLoader extends AsyncTask<Void, Void, CardFragment> {

        @Override
        protected CardFragment doInBackground(Void... params) {

            CardFragment cardFragment = mPresenter.getCurrentCardFragment();
            return isCancelled() ? null : cardFragment;
        }

        @Override
        protected void onPostExecute(CardFragment cardFragment) {

            if(cardFragment!= null && !isCancelled()) {
                ProgressBar progressBar = (ProgressBar)getActivity()
                        .findViewById(R.id.progress_bar_in_game);
                ((ViewManager)progressBar.getParent()).removeView(progressBar);
                View placeHolder = getActivity().findViewById(R.id
                        .place_holder_view_in_game);
                ((ViewManager)placeHolder.getParent()).removeView(placeHolder);

                FragmentTransaction transaction = getFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.card_frame_in_game, cardFragment);
                transaction.commit();
            }
        }
    }
}
