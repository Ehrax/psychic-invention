package de.in.uulm.map.quartett.gameend;

<<<<<<< HEAD
import android.support.v4.app.Fragment;
=======
>>>>>>> develop
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.orm.dsl.NotNull;

import de.in.uulm.map.quartett.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by jona on 1/10/17.
 */

public class GameEndFragment extends Fragment implements GameEndContract.View {

    private GameEndContract.Presenter mPresenter;

    private TextView mStatusText;

    private TextView mSubStatusText;

    private ImageButton mRestartButton;

    private ImageButton mChangeButton;

    private ImageButton mMainMenuButton;

    /**
     * This will be called by the android API. It is used to create the View of
     * this Fragment and to connect the View components with the presenter.
     *
     * @param inflater           the inflater used to create the layout
     * @param container          the view the layout will be inflated in
     * @param savedInstanceState some state, unused
     * @return the inflated view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_game_end, container, false);

        mStatusText = (TextView) v.findViewById(R.id.end_status_text);

        mSubStatusText = (TextView) v.findViewById(R.id.end_status_sub_text);

        mRestartButton = (ImageButton) v.findViewById(R.id.btn_restart);

        mChangeButton = (ImageButton) v.findViewById(R.id.btn_restart_settings);

        mMainMenuButton = (ImageButton) v.findViewById(R.id.btn_main_menu);

        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPresenter.onRestartClicked();
            }
        });

        mChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPresenter.onSettingsClicked();
            }
        });

        mMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPresenter.onMainMenuClicked();
            }
        });

        mPresenter.onViewCreated();

        return v;
    }

    /**
     * Setter for the presenter component.
     */
    @Override
    public void setPresenter(@NotNull GameEndContract.Presenter presenter) {

        mPresenter = checkNotNull(presenter);
    }

    /**
     * Setter for the game state. Will update the central status text.
     * @param endState the game end state
     */
    @Override
    public void setStatus(GameEndState endState) {

        int id;

        switch (endState) {
            case WIN:
                id = R.string.game_end_win;
                break;
            case LOSE:
                id = R.string.game_end_lose;
                break;
            default:
                id = R.string.game_end_draw;
                break;
        }

        mStatusText.setText(id);
    }

    /**
     * Setter for the status text below the main message.
     * @param text the new sub status text
     */
    @Override
    public void setSubStatusText(String text) {

        mSubStatusText.setText(text);
    }
}
