package de.in.uulm.map.quartett.mainmenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.ActivityUtils;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MainMenuPresenter mMainMenuPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        //Setting the toolbar (we can use the same toolbar for all activites)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
         Setting the ActionBarDrawerToggle which is an easy way to connect the
         toolbar and the drawer. So clicking the toolbar logo will open the
         drawer like mentioned in the android design guidelines:
         https://material.io/guidelines/patterns/navigation-drawer.html
         Just replace DrawerLayout id with the one you want to use in other
         activities.
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id
                .drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /*
        Setting this activity as the Listener for the Drawer
        (maybe adding methods to start the new activities to BasePresenter
        interface?)
         */
        NavigationView navigationView = (NavigationView) findViewById(R.id
                .drawer_view);
        navigationView.setNavigationItemSelectedListener(this);

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

    }

    /**
     * Necessary to override back arrow behaviour if the drawer is opened.
     * This will close the drawer instead of going back to the last activity.
     */
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id
                .drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Simple Method to handle clicks on the drawer.
     * Calls presenter methods to start the new activities then closes the
     * drawer.
     *
     * @param item the selected Drawer Item
     * @return ??
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id
                .drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
