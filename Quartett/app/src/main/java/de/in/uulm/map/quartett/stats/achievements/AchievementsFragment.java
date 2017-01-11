package de.in.uulm.map.quartett.stats.achievements;

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

public class AchievementsFragment extends Fragment implements StatsContract
        .AchievementsView {

    StatsContract.AchievementsPresenter mPresenter;

    public static AchievementsFragment newInstance() {

        return new AchievementsFragment();
    }

    @Override
    public void setPresenter(StatsContract.AchievementsPresenter presenter) {

        this.mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_stats_achievments, container, false);

        return v;
    }
}
