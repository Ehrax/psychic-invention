package de.in.uulm.map.quartett.stats.stats;

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

public class StatsFragment extends Fragment implements StatsContract.StatsView {

    StatsContract.StatsPresenter mPresenter;

    public static StatsFragment newInstance() {

        return new StatsFragment();
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

        return v;
    }
}
