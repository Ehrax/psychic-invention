package de.in.uulm.map.quartett.settings;

import android.app.Fragment;
import android.os.Bundle;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by alexanderrasputin on 03.01.17.
 */

public class SettingsFragment extends Fragment implements SettingsContract.View {

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
    }
}
