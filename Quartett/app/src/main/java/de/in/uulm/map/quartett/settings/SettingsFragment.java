package de.in.uulm.map.quartett.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.in.uulm.map.quartett.R;

import static com.google.common.base.Preconditions.checkNotNull;

public class SettingsFragment extends PreferenceFragment implements
        SettingsContract.View, SharedPreferences.OnSharedPreferenceChangeListener {

    private SettingsContract.Presenter mPresenter;

    public static SettingsFragment newInstance() {

        return new SettingsFragment();
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {

        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        Context context = getActivity();

        /**
         * setting summary of preferences
         */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sp.getString("user_name", "NULL");
        String gameMode = sp.getString("game_mode", "NULL");

        EditTextPreference userNamePreference = (EditTextPreference)
                findPreference("user_name");

        ListPreference gameModePreference = (ListPreference)
                findPreference("game_mode");

        userNamePreference.setSummary(userName);
        gameModePreference.setSummary(gameMode);

        /**
         * registering onSharedPreferenceChangeListener
         */
        sp.registerOnSharedPreferenceChangeListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    /**
     * TODO comment here
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case "user_name": {
                String userName = sharedPreferences.getString(key, "NULL");
                EditTextPreference userNamePreference = (EditTextPreference)
                        findPreference("user_name");

                userNamePreference.setSummary(userName);
            }
            case "game_mode": {
                String gameMode = sharedPreferences.getString("game_mode",
                        "NULL");
                ListPreference gameModePreference = (ListPreference)
                        findPreference("game_mode");

                gameModePreference.setSummary(gameMode);
            }
        }
    }
}
