package de.in.uulm.map.quartett.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;

public class SettingsPresenter implements SettingsContract.Presenter {

    @NonNull
    private final SettingsContract.View mView;
    private final Context mContext;

    public SettingsPresenter(@NonNull SettingsContract.View settingsView,
                             Context ctx) {

        mView = settingsView;
        mContext = ctx;
    }

    @Override
    public void start() {
    }
}
