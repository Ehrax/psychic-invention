package de.in.uulm.map.quartett.stats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by alexanderrasputin on 11.01.17.
 */
public class StatsFragmentPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private Context mContext;

    private String mTitles[] = new String[]{"Stats", "Achievements", "Ranking"};

    /**
     * TODO comment here
     */
    public StatsFragmentPageAdapter(FragmentManager fm, Context ctx,
                                    List<Fragment> fragments) {

        super(fm);
        this.mContext = ctx;
        this.mFragments = fragments;
    }

    /**
     * TODO comment here
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    /**
     * TODO commet here
     * @return
     */
    @Override
    public int getCount() {

        return mFragments.size();
    }

    /**
     * TODO comment here
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
