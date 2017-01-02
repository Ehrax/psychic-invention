package de.in.uulm.map.quartett.mainmenu;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.factory.EntityFactory;
import de.in.uulm.map.quartett.util.ActivityUtils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;


public class MainMenuActivity extends DrawerActivity {

    private MainMenuPresenter mMainMenuPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        Call super.onCreate to initialise the navigation drawer and set the
        contentView
         */
        super.onCreate(savedInstanceState);

        /*Initialise Fragment and set Presenter (contentFrame is a
        FrameLayout in app_bar.xml)*/
        MainMenuFragment mainMenuFragment = (MainMenuFragment)
                getFragmentManager().findFragmentById(R.id
                        .contentFrame);
        if (mainMenuFragment == null) {
            mainMenuFragment = MainMenuFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getFragmentManager(),
                    mainMenuFragment, R.id.contentFrame);
        }

        mMainMenuPresenter = new MainMenuPresenter(mainMenuFragment, this);
        mainMenuFragment.setPresenter(mMainMenuPresenter);

        importDecksFromAssets();
    }

    private void importDecksFromAssets() {

        try {
            String[] strings = getAssets().list("decks");

            for (String s : strings) {
                EntityFactory e = new EntityFactory(this);
                e.importDeckFromAssets("decks/"+s+"/"+s+".json");
                Log.d("Deck", ""+new File(
                                "file:///android_asset/decks/"+s+"/"+s+".json")
                                .lastModified());
            }
        } catch (IOException | JSONException e) {
            Toast.makeText(this, "Decks konnten nicht geladen werden!",
                    Toast.LENGTH_LONG);
        }
    }


}
