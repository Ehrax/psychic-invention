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
import de.in.uulm.map.quartett.data.Achievement;
import de.in.uulm.map.quartett.data.Highscore;
import de.in.uulm.map.quartett.data.Statistic;
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

    private ArrayList<Fragment> mFragments;

    /**
     * base constructor for returning a new instance of TabFactoryFragment
     *
     * @return see TabFactoryFragment
     */
    public static TabFactoryFragment newInstance() {

        return new TabFactoryFragment();
    }

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
        RankingPresenter rankingPresenter = new RankingPresenter
                (rankingFragment, getActivity());
        rankingFragment.setPresenter(rankingPresenter);

        // adding mFragments to array list which will be passed to the adapter
        mFragments = new ArrayList<>();
        mFragments.add(statsFragment);
        mFragments.add(achievementsFragment);
        mFragments.add(rankingFragment);

        ViewPager viewPager = (ViewPager) getActivity().
                findViewById(R.id.stats_viewpager);

        viewPager.setAdapter(new StatsFragmentPageAdapter(getActivity()
                .getSupportFragmentManager(), getActivity(),
                mFragments));


        /**
         * this is needed because onResume is not called if a tab has changed,
         * onPageSelected calls the corresponding fragment which is now visible
         */
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Fragment fragment = mFragments.get(position);
                Bundle args = fragment.getArguments();

                String title = args.getString(TabFactoryFragment.TAB_TITLE);

                switch (title) {
                    case StatsFragment.TAB_STATISTICS: {
                        StatsFragment statsFragment = (StatsFragment) fragment;
                        statsFragment.fragmentBecomeVisible();
                        break;
                    }

                    case AchievementsFragment.TAB_ACHIEVEMENTS: {
                        AchievementsFragment achievementsFragment =
                                (AchievementsFragment) fragment;
                        achievementsFragment.fragmentBecomeVisible();
                        break;
                    }

                    case RankingFragment.TAB_RANKING: {
                        RankingFragment rankingFragment = (RankingFragment) fragment;
                        rankingFragment.fragmentBecomeVisible();
                        break;
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


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
