package de.in.uulm.map.quartett.gamesettings;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import de.in.uulm.map.quartett.gallery.GalleryActivity;
import de.in.uulm.map.quartett.gallery.GalleryMode;


/**
 * Created by Jona on 08.01.2017.
 */

public class GameSettingsPresenter implements GameSettingsContract.Presenter {

    /**
     * Reference to view so that the view can be asked for the appropriate
     * values.
     */
    GameSettingsContract.View mView;

    /**
     * Abstracted reference to the enclosing Activity.
     */
    GameSettingsContract.Backend mBackend;

    /**
     * Context needed for intent construction.
     */
    Context mContext;

    public static final String NAME = "gs-name";
    public static final String LIMIT = "gs-limit";
    public static final String MODE = "gs-mode";
    public static final String DECK = "gs-deck";
    public static final String LEVEL = "gs-level";

    /**
     * Simple constructor to initialize members.
     *
     * @param mBackend the backend
     * @param mContext current package context
     */
    GameSettingsPresenter(GameSettingsContract.View mView,
                          GameSettingsContract.Backend mBackend,
                          Context mContext) {

        this.mView = mView;
        this.mBackend = mBackend;
        this.mContext = mContext;
    }

    /**
     * This could be used for further initialization but ...
     */
    @Override
    public void start() {

        // ... there is nothing to do here.
    }

    /**
     * This method will be triggered whenever the name is changed. The okButton
     * is also passed to the method as the state of this button depends on
     * whether name has be entered or not.
     */
    @Override
    public void onNameChanged(String name, Button okButton) {

        okButton.setEnabled(name != null && name.length() > 0);
    }

    /**
     * This method  will be called whenever the ok button is pressed. It will
     * end the Activity and will jump to the next one in line.
     */
    @Override
    public void onOkPressed() {

        Intent intent = new Intent(mContext, GalleryActivity.class);
        intent.putExtra(NAME, mView.getName());
        intent.putExtra(LIMIT, mView.getLimit());
        intent.putExtra(MODE, mView.getMode());
        intent.putExtra(LEVEL,mView.getLevel());
        intent.putExtra("mode", GalleryMode.CHOOSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        mBackend.nextActivity(intent);
    }
}
