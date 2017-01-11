package de.in.uulm.map.quartett.stats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.stats.achievements.AchievementsFragment;
import de.in.uulm.map.quartett.stats.ranking.RankingFragment;
import de.in.uulm.map.quartett.stats.stats.StatsFragment;

import java.util.ArrayList;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class TabFactoryFragment extends Fragment {

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
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(StatsFragment.newInstance());
        fragments.add(RankingFragment.newInstance());
        fragments.add(AchievementsFragment.newInstance());

        ViewPager viewPager = (ViewPager) getActivity().
                findViewById(R.id.stats_viewpager);

        viewPager.setAdapter(new StatsFragmentPageAdapter(getActivity()
                .getSupportFragmentManager(), getContext(), fragments));

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
