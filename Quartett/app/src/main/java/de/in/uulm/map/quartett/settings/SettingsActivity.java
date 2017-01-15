package de.in.uulm.map.quartett.settings;

import android.os.Bundle;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.ActivityUtils;


public class SettingsActivity extends DrawerActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*
        Calls super.onCreate to initialise the navigation drawer and set the
        contentView
         */
        super.onCreate(savedInstanceState);

        SettingsFragment settingsFragment = (SettingsFragment)
                getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (settingsFragment == null) {
            settingsFragment = SettingsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    settingsFragment, R.id.contentFrame);
        }
    }
}
