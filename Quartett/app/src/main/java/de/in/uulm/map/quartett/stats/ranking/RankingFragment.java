package de.in.uulm.map.quartett.stats.ranking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.stats.StatsContract;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class RankingFragment extends Fragment implements StatsContract.RankingView {

    private StatsContract.RankingPresenter mPresenter;

    public static RankingFragment newInstance() {

        return new RankingFragment();
    }

    @Override
    public void setPresenter(StatsContract.RankingPresenter presenter) {

        this.mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_stats_ranking, container, false);

        return v;
    }
}
