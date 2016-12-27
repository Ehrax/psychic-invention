package de.in.uulm.map.quartett.mainmenu;

import android.app.Activity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.data.Deck;
import de.in.uulm.map.quartett.factory.EntityFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        /**

        StringWriter stringWriter = new StringWriter();

        try(InputStream in = getResources().openRawResource(R.raw.tuning)) {

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(in));

            char[] buffer = new char[1024];

            int n;
            while ((n = reader.read(buffer)) != -1) {
                stringWriter.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        EntityFactory entityFactory = new EntityFactory(this);

        Deck deck = null;

        try {
            deck = entityFactory.getDeck(new JSONObject(stringWriter.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

         */

        EntityFactory entityFactory = new EntityFactory(this);

        try {
            entityFactory.getDeckFromAssets("decks/bikes/bikes.json");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
