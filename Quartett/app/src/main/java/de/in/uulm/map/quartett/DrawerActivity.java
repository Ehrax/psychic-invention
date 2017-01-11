package de.in.uulm.map.quartett;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import de.in.uulm.map.quartett.gallery.GalleryActivity;
import de.in.uulm.map.quartett.mainmenu.MainMenuActivity;
import de.in.uulm.map.quartett.stats.StatsActivity;

/**
 * Base class for all activities with navigation drawer. Just extend this Class
 * with your own activity and call super.onCreate(savedInstanceState) from the
 * onCreate Method of your own activity.
 */
public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        //Setting the toolbar (we can use the same toolbar for all activities)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
         Setting the ActionBarDrawerToggle which is an easy way to connect the
         toolbar and the drawer. So clicking the toolbar logo will open the
         drawer like mentioned in the android design guidelines:
         https://material.io/guidelines/patterns/navigation-drawer.html
         */
        mDrawer = (DrawerLayout) findViewById(R.id
                .drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer,
                toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();


        //Setting this activity as the Listener for the Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id
                .drawer_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    /**
     * Necessary to override back arrow behaviour if the drawer is opened. This
     * will close the drawer instead of going back to the last activity.
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
     * This Method handles the navigation with the drawer. It simply starts the
     * chosen activity. If you add a new activity just add a startActivity call
     * here.
     *
     * @param item the selected menu item
     * @return ?
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_achievements:
                break;
            case R.id.nav_editor:
                break;
            case R.id.nav_gallery:
                if (!this.getClass().getSimpleName().equals
                        ("GalleryActivity")) {
                    Intent intent = new Intent(this, GalleryActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(this);

                    startActivity(intent, options.toBundle());
                }
                break;
            case R.id.nav_help:
                break;
            case R.id.nav_main_menu:
                if (!this.getClass().getSimpleName().equals("MainMenuActivity")) {
                    Intent intent = new Intent(this, MainMenuActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(this);

                    startActivity(intent, options.toBundle());
                }
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_statistic:
                if (!this.getClass().getSimpleName().equals("StatsActivity")) {
                    Intent intent = new Intent(this, StatsActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(this);

                    startActivity(intent, options.toBundle());
                }
                break;
            default:
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;

    }
}

