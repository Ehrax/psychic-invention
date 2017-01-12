package de.in.uulm.map.quartett.stats.stats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.stats.StatsContract;
import de.in.uulm.map.quartett.stats.TabFactoryFragment;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class StatsFragment extends Fragment implements StatsContract.StatsView {

    private final static String TAB_STATISTICS = "Stats";

    StatsContract.StatsPresenter mPresenter;

    /**
     * TODO comment here
     * @return
     */
    public static StatsFragment newInstance() {

        StatsFragment statsFragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(TabFactoryFragment.TAB_TITLE, TAB_STATISTICS);
        statsFragment.setArguments(args);

        return statsFragment;
    }

    @Override
    public void setPresenter(StatsContract.StatsPresenter presenter) {

        this.mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_stats_stats, container, false);

        /**
         * getting all TextViews and ArcProgress Circles and passing them to
         * the presenter start method
         */
        ArcProgress arcProgressWin = (ArcProgress) getActivity()
                .findViewById(R.id.stats_win_circle);
        ArcProgress arcProgressAchiev = (ArcProgress) getActivity()
                .findViewById(R
                .id.stats_achievement_circle);
        ArcProgress arcProgressHands = (ArcProgress) getActivity()
                .findViewById(R.id.stats_hands_circle);

        TextView texViewGamesWon = (TextView) getActivity().findViewById(R
                .id.stats_games_won);
        TextView textViewGamesLost = (TextView) getActivity().findViewById(R
                .id.stats_games_lost);

        TextView textViewsHandsWon = (TextView) getActivity().findViewById(R
                .id.stats_hands_won);
        TextView textViewHandsLost = (TextView) getActivity().findViewById(R
                .id.stas_hands_lost);

        mPresenter.start();

        return v;
    }
}
