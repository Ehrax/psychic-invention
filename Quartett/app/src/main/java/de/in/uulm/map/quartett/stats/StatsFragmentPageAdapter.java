package de.in.uulm.map.quartett.stats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import de.in.uulm.map.quartett.stats.achievements.AchievementsFragment;
import de.in.uulm.map.quartett.stats.ranking.RankingFragment;
import de.in.uulm.map.quartett.stats.stats.StatsFragment;

import java.util.List;

/**
 * Created by alexanderrasputin on 11.01.17.
 */
public class StatsFragmentPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private Context mContext;

    private String mTitles[] = new String[]{"Stats", "Achievements", "Ranking"};

    /**
     * basic constructor for the Adapter
     */
    public StatsFragmentPageAdapter(FragmentManager fm, Context ctx,
                                    List<Fragment> fragments) {

        super(fm);
        this.mContext = ctx;
        this.mFragments = fragments;
    }

    /**
     * This method is returning a new fragment if the view has changed
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    /**
     * This method is returning the size of the Fragments list
     * @return
     */
    @Override
    public int getCount() {

        return mFragments.size();
    }

    /**
     * This method is setting the tab titles
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {

        Fragment fragment = mFragments.get(position);
        Bundle args = fragment.getArguments();


        return args.getString(TabFactoryFragment.TAB_TITLE);
    }
}
