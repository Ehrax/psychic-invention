package de.in.uulm.map.quartett.mainmenu;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Attribute;
import de.in.uulm.map.quartett.data.AttributeValue;
import de.in.uulm.map.quartett.data.Card;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.data.Highscore;
import de.in.uulm.map.quartett.data.Image;
import de.in.uulm.map.quartett.util.ActivityUtils;

public class MainMenuActivity extends Activity {

    private MainMenuPresenter mMainMenuPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        MainMenuFragment mainMenuFragment = (MainMenuFragment)
                getFragmentManager().findFragmentById(R.id
                        .mainMenuContentFrame);
        if (mainMenuFragment == null) {
            mainMenuFragment = MainMenuFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    mainMenuFragment, R.id.mainMenuContentFrame);
        }

        mMainMenuPresenter = new MainMenuPresenter(mainMenuFragment, this);
        mainMenuFragment.setPresenter(mMainMenuPresenter);

    }
}
