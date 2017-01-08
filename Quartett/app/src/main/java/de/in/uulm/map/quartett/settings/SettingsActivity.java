package de.in.uulm.map.quartett.settings;

import android.os.Bundle;
import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.ActivityUtils;


public class SettingsActivity extends DrawerActivity {

    private SettingsPresenter mSettingsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*
        Calls super.onCreate to initialise the navigation drawer and set the
        contentView
         */
        super.onCreate(savedInstanceState);

        SettingsFragment settingsFragment = (SettingsFragment) getFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (settingsFragment == null) {
            settingsFragment = SettingsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    settingsFragment, R.id.contentFrame);
        }

        mSettingsPresenter = new SettingsPresenter(settingsFragment, this);
        settingsFragment.setPresenter(mSettingsPresenter);
    }
}
