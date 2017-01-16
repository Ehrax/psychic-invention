package de.in.uulm.map.quartett.start;

import com.google.android.gms.common.api.GoogleApiClient;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.mainmenu.MainMenuActivity;

/**
 * Created by alexanderrasputin on 16.01.17.
 */

public class StartActivity extends AppCompatActivity {

    private static int DURATION = 1000;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API. See
     * https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final ImageView logo = (ImageView) findViewById(R.id.start_logo);

        float fromXscale = logo.getScaleX();
        float toXscale = fromXscale + 0.2f;

        float fromYScale = logo.getScaleY();
        float toYScale = fromYScale + 0.2f;

        final Animation scale = new ScaleAnimation(fromXscale, toXscale,
                fromYScale, toYScale, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        final Animation reScale = new ScaleAnimation(toXscale, fromXscale, toYScale,
                fromYScale, Animation.RELATIVE_TO_SELF, 0.5f, Animation
                .RELATIVE_TO_SELF, 0.5f);

        scale.setDuration(DURATION);
        reScale.setDuration(DURATION);
        reScale.setInterpolator(new BounceInterpolator());

        scale.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                logo.startAnimation(reScale);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });

        reScale.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                startActivity(new Intent(getApplicationContext(),
                        MainMenuActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        logo.startAnimation(scale);
    }

}
