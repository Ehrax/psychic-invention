package de.in.uulm.map.quartett.stats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Achievement;
import de.in.uulm.map.quartett.stats.achievements.AchievementsFragment;
import de.in.uulm.map.quartett.stats.achievements.AchievementsPresenter;
import de.in.uulm.map.quartett.stats.ranking.RankingFragment;
import de.in.uulm.map.quartett.stats.ranking.RankingPresenter;
import de.in.uulm.map.quartett.stats.stats.StatsFragment;
import de.in.uulm.map.quartett.stats.stats.StatsPresenter;

import java.util.ArrayList;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class TabFactoryFragment extends Fragment {


    public static final String TAB_TITLE = "tab_title";

    /**
     * base constructor for returning a new instance of TabFactoryFragment
     *
     * @return see TabFactoryFragment
     */
    public static TabFactoryFragment newInstance() {

        return new TabFactoryFragment();
    }

    /**
     * TODO comment here
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        // creating StatsFragment and his presenter
        StatsFragment statsFragment = StatsFragment.newInstance();
        StatsPresenter statsPresenter = new StatsPresenter(statsFragment,
                getActivity());
        statsFragment.setPresenter(statsPresenter);

        // creating AchievementsFragment and his presenter
        AchievementsFragment achievementsFragment = AchievementsFragment
                .newInstance();
        AchievementsPresenter achievementsPresenter = new
                AchievementsPresenter(achievementsFragment, getActivity());
        achievementsFragment.setPresenter(achievementsPresenter);

        // creating RankingFragment and his presenter
        RankingFragment rankingFragment = RankingFragment.newInstance();
        RankingPresenter rankingPresenter = new RankingPresenter();
        rankingFragment.setPresenter(rankingPresenter);

        // adding fragments to array list which will be passed to the adapter
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(statsFragment);
        fragments.add(achievementsFragment);
        fragments.add(rankingFragment);

        ViewPager viewPager = (ViewPager) getActivity().
                findViewById(R.id.stats_viewpager);

        viewPager.setAdapter(new StatsFragmentPageAdapter(getActivity()
                .getSupportFragmentManager(), getActivity(),
                fragments));

        TabLayout tabLayout = (TabLayout) getActivity().
                findViewById(R.id.stats_sliding_tabs);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.stats_sliding_tabs, container, false);
    }
}
