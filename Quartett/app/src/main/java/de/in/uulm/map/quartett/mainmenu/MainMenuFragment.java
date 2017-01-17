package de.in.uulm.map.quartett.mainmenu;

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
import de.in.uulm.map.quartett.data.LocalGameState;
import de.in.uulm.map.quartett.gallery.GalleryContract;
import de.in.uulm.map.quartett.util.BasePresenter;

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
