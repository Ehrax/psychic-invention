package de.in.uulm.map.quartett.gameend;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private Button mRestartButton;

    private Button mChangeButton;

    private Button mMainMenuButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_game_end, container, false);

        mStatusText = (TextView) v.findViewById(R.id.end_status_text);

        mSubStatusText = (TextView) v.findViewById(R.id.end_status_sub_text);

        mRestartButton = (Button) v.findViewById(R.id.btn_restart);

        mChangeButton = (Button) v.findViewById(R.id.btn_restart_settings);

        mMainMenuButton = (Button) v.findViewById(R.id.btn_main_menu);

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

        return v;
    }

    @Override
    public void setPresenter(@NotNull GameEndContract.Presenter presenter) {

        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setStatusText(String text) {

        mStatusText.setText(text);
    }

    @Override
    public void setSubStatusText(String text) {

        mSubStatusText.setText(text);
    }
}
