package de.in.uulm.map.quartett.stats.stats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.stats.StatsContract;
import de.in.uulm.map.quartett.stats.TabFactoryFragment;
import de.in.uulm.map.quartett.views.BetterArcProgress;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class StatsFragment extends Fragment implements StatsContract.StatsView {

    public final static String TAB_STATISTICS = "Stats";

    private BetterArcProgress arcProgressWin;
    private BetterArcProgress arcProgressAchiev;
    private BetterArcProgress arcProgressHands;
    private TextView textViewGamesWon;
    private TextView textViewGamesLost;
    private TextView textViewHandsWon;
    private TextView textViewHandsLost;

    StatsContract.StatsPresenter mPresenter;

    /**
     * TODO comment here
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
        arcProgressWin = (BetterArcProgress)
                v.findViewById(R.id.stats_win_circle);
        arcProgressAchiev = (BetterArcProgress)
                v.findViewById(R.id.stats_achievement_circle);
        arcProgressHands = (BetterArcProgress)
                v.findViewById(R.id.stats_hands_circle);

        textViewGamesWon = (TextView)
                v.findViewById(R.id.stats_games_won);
        textViewGamesLost = (TextView)
                v.findViewById(R.id.stats_games_lost);
        textViewHandsWon = (TextView)
                v.findViewById(R.id.stats_hands_won);
        textViewHandsLost = (TextView)
                v.findViewById(R.id.stas_hands_lost);

        mPresenter.start();

        mPresenter.setArcProgressWin(arcProgressWin);
        mPresenter.setArcProgressAchiev(arcProgressAchiev);
        mPresenter.setArcProgressHands(arcProgressHands);

        mPresenter.setTextGamesWon(textViewGamesWon);
        mPresenter.setTextGamesLost(textViewGamesLost);
        mPresenter.setTextHandsWon(textViewHandsWon);
        mPresenter.setTextHandsLost(textViewHandsLost);

        return v;
    }

    @Override
    public void fragmentBecomeVisible() {

        mPresenter.setArcProgressWin(arcProgressWin);
        mPresenter.setArcProgressAchiev(arcProgressAchiev);
        mPresenter.setArcProgressHands(arcProgressHands);

        mPresenter.setTextGamesWon(textViewGamesWon);
        mPresenter.setTextGamesLost(textViewGamesLost);
        mPresenter.setTextHandsWon(textViewHandsWon);
        mPresenter.setTextHandsLost(textViewHandsLost);
    }
}
