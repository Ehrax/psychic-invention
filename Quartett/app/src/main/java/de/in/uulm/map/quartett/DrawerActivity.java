package de.in.uulm.map.quartett;

import android.app.ActivityOptions;
import android.content.Intent;

import android.content.pm.ActivityInfo;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;

import android.support.v4.app.ActivityOptionsCompat;

import android.support.v4.content.SharedPreferencesCompat;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.in.uulm.map.quartett.data.Achievement;
import de.in.uulm.map.quartett.gallery.GalleryActivity;
import de.in.uulm.map.quartett.mainmenu.MainMenuActivity;

import de.in.uulm.map.quartett.stats.StatsActivity;

import de.in.uulm.map.quartett.settings.SettingsActivity;
import de.in.uulm.map.quartett.settings.SettingsFragment;
import de.in.uulm.map.quartett.stats.TabFactoryFragment;
import de.in.uulm.map.quartett.stats.achievements.AchievementsFragment;
import de.in.uulm.map.quartett.stats.stats.StatsFragment;
import de.in.uulm.map.quartett.views.CircularImageView;

import java.io.IOException;


/**
 * Base class for all activities with navigation drawer. Just extend this Class
 * with your own activity and call super.onCreate(savedInstanceState) from the
 * onCreate Method of your own activity.
 */
public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout mDrawer;
    private CircularImageView mProfilePic;
    private TextView mUserTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

        //Setting profile pic
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences
                (this);

        View drawerHeader = getLayoutInflater().inflate(R.layout
                .drawer_header, (ViewGroup) this.mDrawer.findViewById(R.id
                .drawer_view));
        mProfilePic = (CircularImageView)
                drawerHeader.findViewById
                        (R.id.img_drawer_profile_pic_drawer);
        if (sp.contains(SettingsFragment.PROFILE_URI)) {
            mProfilePic.setImageURI(Uri.parse(sp.getString(SettingsFragment
                    .PROFILE_URI, null)));
        }

        mUserTextView = (TextView) drawerHeader.findViewById
                (R.id.txt_drawer_username);

        if (sp.contains("user_name")) {
            mUserTextView.setText(sp.getString("user_name", "Quartet"));
        }

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
                if (!this.getClass().getSimpleName().equals("StatsActivity")) {
                    Intent intent = new Intent(this, StatsActivity.class);
                    intent.putExtra(TabFactoryFragment.TAB_TITLE,
                            AchievementsFragment.TAB_ACHIEVEMENTS);
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(this);

                    startActivity(intent, options.toBundle());
                }
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
                if (!this.getClass().getSimpleName().equals("SettingsActivity")) {
                    Intent intent= new Intent(this,SettingsActivity.class);
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(this,mProfilePic,
                                    "profile_pic_transition");
                    startActivity(intent,options.toBundle());
                }
                break;
            case R.id.nav_statistic:
                if (!this.getClass().getSimpleName().equals("StatsActivity")) {
                    Intent intent = new Intent(this, StatsActivity.class);
                    intent.putExtra(TabFactoryFragment.TAB_TITLE,
                            StatsFragment.TAB_STATISTICS);
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

