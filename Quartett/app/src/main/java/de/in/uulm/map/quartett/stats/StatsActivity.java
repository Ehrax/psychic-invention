package de.in.uulm.map.quartett.stats;

import android.content.Intent;
import android.os.Bundle;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.ActivityUtils;

/**
 * Created by alexanderrasputin on 11.01.17.
 */

public class StatsActivity extends DrawerActivity {

    /**
     * This will be called by the Android API. The function is used to attach
     * the GameSettingsFragment ot the GameSettingsActivity and make the
     * connections between View and Presenter.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        TabFactoryFragment tabFactoryFragment = (TabFactoryFragment)
                getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (tabFactoryFragment == null) {
            tabFactoryFragment = TabFactoryFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    tabFactoryFragment, R.id.contentFrame);
        }
    }
}
