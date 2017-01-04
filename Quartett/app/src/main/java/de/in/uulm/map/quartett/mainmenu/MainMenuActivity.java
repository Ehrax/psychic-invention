package de.in.uulm.map.quartett.mainmenu;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import de.in.uulm.map.quartett.DrawerActivity;
import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.factory.EntityImportTask;
import de.in.uulm.map.quartett.util.ActivityUtils;


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
                getSupportFragmentManager().findFragmentById(R.id
                        .contentFrame);
        if (mainMenuFragment == null) {
            mainMenuFragment = MainMenuFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    mainMenuFragment, R.id.contentFrame);
        }

        mMainMenuPresenter = new MainMenuPresenter(mainMenuFragment, this);
        mainMenuFragment.setPresenter(mMainMenuPresenter);

        new EntityImportTask(this, new EntityImportTask.Callback() {
            @Override
            public void onImportFinished() {
                Log.d("ALLDECKS",Deck.findAll(Deck.class).toString());
                // do stuff with the decks and cards here ...
                // in this callback all decks are loaded, i promise :P
            }
        }).execute();




    }


}
