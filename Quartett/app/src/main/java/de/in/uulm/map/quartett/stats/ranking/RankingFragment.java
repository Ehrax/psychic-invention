package de.in.uulm.map.quartett.stats.ranking;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.stats.StatsContract;
import de.in.uulm.map.quartett.stats.TabFactoryFragment;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class RankingFragment extends Fragment implements StatsContract.RankingView {

    public static final String TAB_RANKING = "Ranking";

    private StatsContract.RankingPresenter mPresenter;

    public static RankingFragment newInstance() {

        RankingFragment rankingFragment = new RankingFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TabFactoryFragment.TAB_TITLE, TAB_RANKING);
        rankingFragment.setArguments(bundle);

        return rankingFragment;
    }

    @Override
    public void setPresenter(StatsContract.RankingPresenter presenter) {

        this.mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_stats_ranking,
                container, false);

        mPresenter.start();

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id
                .stats_ranking_recycler_view);

        RankingAdapter adapter = new RankingAdapter
                (mPresenter, getActivity());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    @Override
    public void fragmentBecomeVisible() {

    }
}
