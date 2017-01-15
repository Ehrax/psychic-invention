package de.in.uulm.map.quartett.gamesettings;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

<<<<<<< HEAD
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.game.GameActivity;
=======
import de.in.uulm.map.quartett.gallery.GalleryActivity;
import de.in.uulm.map.quartett.gallery.GalleryMode;
>>>>>>> develop

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
    public static final String POINTS = "gs-points";
    public static final String TIME = "gs-time";
    public static final String ROUNDS = "gs-rounds";
    public static final String MODE = "gs-mode";

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

<<<<<<< HEAD
        Intent intent = new Intent(mContext, GameActivity.class);
        intent.putExtra(NAME, mView.getName());
        intent.putExtra(POINTS, mView.getPoints());
        intent.putExtra(TIME, mView.getTime());
        intent.putExtra(ROUNDS, mView.getRounds());
        intent.putExtra(MODE, mView.getMode());
=======
        Intent intent = new Intent(mContext, GalleryActivity.class);
        intent.putExtra("gs-name", mView.getName());
        intent.putExtra("gs-mode", mView.getMode());
        intent.putExtra("gs-limit", mView.getLimit());
        intent.putExtra("gs-level", mView.getLevel());
        intent.putExtra("mode", GalleryMode.CHOOSE);
>>>>>>> develop

        mBackend.nextActivity(intent);
    }
}
