package de.in.uulm.map.quartett.gamesettings;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.orm.dsl.NotNull;

import de.in.uulm.map.quartett.R;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Jona on 08.01.2017.
 */

public class GameSettingsFragment extends Fragment implements GameSettingsContract.View {

    private GameSettingsContract.Presenter mPresenter;

    private EditText mEditTextName;

    private RadioGroup mRadioGroup;

    private NumberPicker mPointsPicker;

    private NumberPicker mTimePicker;

    private NumberPicker mRoundPicker;

    /**
     * This function will be called by the Android Framework when the View of
     * this Fragment should be loaded. It create the corresponding view and
     * connects the elements to the presenter.
     *
     * @param inflater           a LayoutInflater given by the Android
     *                           Framework
     * @param container          a View in which the Layout will be inflated
     * @param savedInstanceState some State, unused
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(
                R.layout.fragment_game_settings, container, false);

        final Button button = (Button) v.findViewById(R.id.btn_ok);

        mEditTextName = (EditText) v.findViewById(R.id.edit_text_name);

        mRadioGroup = (RadioGroup) v.findViewById(R.id.rg_game_mode);

        mPointsPicker = (NumberPicker) v.findViewById(R.id.points_mode_picker);

        mTimePicker = (NumberPicker) v.findViewById(R.id.time_mode_picker);

        mRoundPicker = (NumberPicker) v.findViewById(R.id.round_mode_picker);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPresenter.onOkPressed();
            }
        });

        mEditTextName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mEditTextName.setCursorVisible(true);
            }
        });

        mEditTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_DONE) {
                    mEditTextName.clearFocus();
                    mEditTextName.setCursorVisible(false);
                }

                return false;
            }
        });

        mEditTextName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence,
                                          int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence,
                                      int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {

                mPresenter.onNameChanged(editable.toString(), button);
            }
        });

        mPointsPicker.setMinValue(10);
        mPointsPicker.setMaxValue(100);
        mPointsPicker.setValue(20);

        mTimePicker.setMinValue(5);
        mTimePicker.setMaxValue(60);
        mTimePicker.setValue(10);

        mTimePicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {

                return i + " min";
            }
        });

        mRoundPicker.setMinValue(2);
        mRoundPicker.setMaxValue(100);
        mRoundPicker.setValue(10);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                mPointsPicker.setVisibility(
                        i == R.id.rb_mode_points ? View.VISIBLE : View.GONE);
                mTimePicker.setVisibility(
                        i == R.id.rb_mode_time ? View.VISIBLE : View.GONE);
                mRoundPicker.setVisibility(
                        i == R.id.rb_mode_rounds ? View.VISIBLE : View.GONE);
            }
        });

        return v;
    }

    /**
     * This function is used to connect the View with the Presenter element.
     *
     * @param presenter the presetner this Fragment should talk to
     */
    @Override
    public void setPresenter(@NotNull GameSettingsContract.Presenter presenter) {

        this.mPresenter = checkNotNull(presenter);
    }

    /**
     * Getter for the currently entered name.
     * @return the player name
     */
    @Override
    public String getName() {

        return mEditTextName.getText().toString();
    }

    /**
     * Getter for the currently selected points.
     * @return selected points
     */
    @Override
    public int getPoints() {

        return mPointsPicker.getValue();
    }

    /**
     * Getter for the currently selected time.
     * @return selected time in minutes
     */
    @Override
    public int getTime() {

        return mTimePicker.getValue();
    }

    /**
     * Getter for the currently selected rounds.
     * @return selected round
     */
    @Override
    public int getRounds() {

        return mPointsPicker.getValue();
    }

    /**
     * Getter for the currently selected mode.
     * @return selected mode
     */
    @Override
    public GameMode getMode() {

        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.rb_mode_insane:
                return GameMode.INSANE;
            case R.id.rb_mode_time:
                return GameMode.TIME;
            case R.id.rb_mode_rounds:
                return GameMode.ROUNDS;
            default:
                return GameMode.POINTS;
        }
    }
}
