package de.in.uulm.map.quartett.settings;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;

import de.in.uulm.map.quartett.util.BasePresenter;
import de.in.uulm.map.quartett.util.BaseView;

/**
 * Created by alexanderrasputin on 03.01.17.
 */

public interface SettingsContract {

    interface Presenter extends BasePresenter {
    }

    interface View extends BaseView<Presenter> {

    }
}
