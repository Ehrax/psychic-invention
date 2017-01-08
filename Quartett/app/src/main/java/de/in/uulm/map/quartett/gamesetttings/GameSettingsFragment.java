package de.in.uulm.map.quartett.gamesetttings;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

    /**
     * This function is call be the Android Framework when the Activity this
     * Fragment is assigned to is created. This can be used to build connections
     * with the presenter or to initialize GUI elements.
     *
     * @param savedInstanceState some State
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Activity a = getActivity();

        final Button ok_button = (Button) a.findViewById(R.id.btn_ok);

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPresenter.onOkPressed();
            }
        });

        final EditText edit_name = (EditText) a.findViewById(R.id.edit_text_name);

        edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit_name.setCursorVisible(true);
            }
        });

        edit_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_DONE) {
                    edit_name.clearFocus();
                    edit_name.setCursorVisible(false);
                }

                return false;
            }
        });

        edit_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence,
                                          int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence,
                                      int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {

                mPresenter.onNameChanged(editable.toString(), ok_button);
            }
        });

        RadioGroup rg_game_mode =
                (RadioGroup) a.findViewById(R.id.rg_game_mode);

        final NumberPicker points_picker =
                (NumberPicker) a.findViewById(R.id.points_mode_picker);

        points_picker.setMinValue(10);
        points_picker.setMaxValue(100);
        points_picker.setValue(20);

        points_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

                mPresenter.onPointsChanged(i);
            }
        });

        final NumberPicker time_picker =
                (NumberPicker) a.findViewById(R.id.time_mode_picker);

        time_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {

                return i + " min";
            }
        });

        time_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

                mPresenter.onTimeChanged(i);
            }
        });

        time_picker.setMinValue(5);
        time_picker.setMaxValue(60);
        time_picker.setValue(10);

        rg_game_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                points_picker.setVisibility(
                        i == R.id.rb_mode_points ? View.VISIBLE : View.GONE);
                time_picker.setVisibility(
                        i == R.id.rb_mode_time ? View.VISIBLE : View.GONE);
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    /**
     * This function will be called by the Android Framework when the View of
     * this Fragment should be loaded. It create the corresponding view and
     * simply returns it.
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

        return inflater.inflate(
                R.layout.fragment_game_settings, container, false);
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
}
