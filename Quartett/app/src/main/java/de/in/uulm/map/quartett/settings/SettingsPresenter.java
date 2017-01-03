package de.in.uulm.map.quartett.settings;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by alexanderrasputin on 03.01.17.
 */

public class SettingsPresenter implements SettingsContract.Presenter {

    @NonNull
    private final SettingsContract.View mView;
    private final Context ctx;

    public SettingsPresenter(@NonNull SettingsContract.View settingsView,
                             Context ctx) {

        mView = settingsView;
        this.ctx = ctx;
    }

    @Override
    public void start() {

    }
}
