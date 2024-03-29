package de.in.uulm.map.quartett.mainmenu;

import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Achievement;
import de.in.uulm.map.quartett.data.LocalGameState;
import de.in.uulm.map.quartett.gallery.GalleryContract;
import de.in.uulm.map.quartett.util.BasePresenter;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by alex on 12/17/16.
 */

public class MainMenuFragment extends Fragment implements MainMenuContract.View {

    private MainMenuContract.Presenter mPresenter;

    public static MainMenuFragment newInstance() {

        return new MainMenuFragment();
    }

    @Override
    public void setPresenter(@NonNull MainMenuContract.Presenter presenter) {

        mPresenter = checkNotNull(presenter);
    }

    /**
     * This method is called automatically by the AndroidOS. Setting the
     * onClickListeners for the MainMenu Buttons. They call the matching
     * presenter methods to start the new activities.
     */
    @Override
    public void onActivityCreated(Bundle savedInstance) {

        super.onActivityCreated(savedInstance);
    }

    @Override
    public void onResume() {

        super.onResume();
        setGameContinueButtonVisibility(getView());
    }

    private void setGameContinueButtonVisibility(View view) {

        LinearLayout layoutContinue =
                (LinearLayout) view.findViewById(R.id.layout_continue_game);

        if (LocalGameState.listAll(LocalGameState.class).isEmpty()) {
            layoutContinue.setVisibility(View.GONE);
        } else {
            layoutContinue.setVisibility(View.VISIBLE);
            Button button = (Button) view.findViewById(R.id.btn_continue_game);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mPresenter.continueLocalGame();
                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<Achievement> achievements = Achievement.listAll(Achievement.class);

        if (achievements.isEmpty()) {
            Achievement achievement = new Achievement();
            achievement.mTitle = "Beginner";
            achievement.mDescription = "Win 5 games!";
            achievement.mValue = 2;
            achievement.mTargetValue = 5;
            achievement.save();

            Achievement achievement1 = new Achievement();
            achievement1.mTitle = "Unlucky";
            achievement1.mDescription = "Lose 10 games in a row!";
            achievement1.mValue = 4;
            achievement1.mTargetValue = 10;
            achievement1.save();

            Achievement achievement2 = new Achievement();
            achievement2.mTitle = "Cheater";
            achievement2.mDescription = "Win a game without loosing once!";
            achievement2.mValue = 0;
            achievement2.mTargetValue = 1;
            achievement2.save();

            Achievement achievement3 = new Achievement();
            achievement2.mTitle = "K.O.";
            achievement2.mDescription = "Win a game by winning all cards!";
            achievement2.mValue = 0;
            achievement2.mTargetValue = 1;
            achievement2.save();

        }

        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        ImageButton btnNewLocalGame = (ImageButton) view.findViewById
                (R.id
                        .btn_new_local_game);
        ImageButton btnNewOnlineGame = (ImageButton) view.findViewById
                (R.id
                        .btn_new_online_game);
        ImageButton btnAchievements = (ImageButton) view.findViewById
                (R.id
                        .btn_achievements);
        ImageButton btnSettings = (ImageButton) view.findViewById(R.id
                .btn_settings);

        btnNewLocalGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPresenter.startNewLocalGame();
            }
        });

        btnNewOnlineGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPresenter.startNewOnlineGame();
            }
        });

        btnAchievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPresenter.startAchievements();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPresenter.startSettings();
            }
        });

        setGameContinueButtonVisibility(view);

        return view;

    }
}
