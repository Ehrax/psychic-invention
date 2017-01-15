package de.in.uulm.map.quartett.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;


import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.theartofdev.edmodo.cropper.CropImage;

import de.in.uulm.map.quartett.R;
import de.in.uulm.map.quartett.util.PermissionHandler;
import de.in.uulm.map.quartett.views.CircularImageView;

import java.io.IOException;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int SELECT_FILE = 0;
    public static final String PROFILE_URI = "profile_photo_uri";

    public static SettingsFragment newInstance() {

        return new SettingsFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        Context context = getActivity();

        /**
         * setting summary of preferences
         */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sp.getString("user_name", "NULL");
        String gameMode = sp.getString("game_mode", "NULL");

        EditTextPreference userNamePreference = (EditTextPreference)
                findPreference("user_name");

        ListPreference gameModePreference = (ListPreference)
                findPreference("game_mode");

        userNamePreference.setSummary(userName);
        gameModePreference.setSummary(gameMode);

        /**
         * registering onSharedPreferenceChangeListener
         */
        sp.registerOnSharedPreferenceChangeListener(this);

        CircularImageView circularImageView = (CircularImageView) getActivity
                ().findViewById(R.id.img_drawer_profile_pic);

        /**
         * setting circular image view picture if shared preference exists
         */
        if (sp.contains(PROFILE_URI)) {
            Bitmap bm = null;
            Uri uri = Uri.parse(sp.getString(PROFILE_URI, "NULL"));

            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity()
                        .getApplicationContext().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            circularImageView.setImageBitmap(bm);
        }

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();
            }
        });
    }

    /**
     * creates a alert dialog where the user can choose to get a image from the
     * gallery
     */
    private void chooseImage() {

        final CharSequence[] items = {"Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                boolean result = PermissionHandler
                        .checkExternalStoragePermission(getActivity());

                if (items[which].equals("Choose from Library")) {
                    if (result) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,
                                "Select File"), SELECT_FILE);
                    }
                } else {
                    dialog.dismiss();
                }

            }
        });

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_FILE: {
                    // starting crop activity to crop image
                    Intent intent = CropImage.activity(data.getData())
                            .getIntent(getContext());
                    startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
                    break;
                }
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                    CropImage.ActivityResult result = CropImage
                            .getActivityResult(data);
                    Uri resultUri = result.getUri();

                    // setting resulted image to image view
                    setImage(resultUri);
                    break;
                }
            }
        }
    }

    /**
     * This method changes circular view and is setting the image path into
     * shard preferences
     *
     * @param resultUri uri from image to be set
     */
    private void setImage(Uri resultUri) {

        Bitmap bm = null;

        try {
            bm = MediaStore.Images.Media.getBitmap(getActivity()
                    .getApplicationContext().getContentResolver(), resultUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CircularImageView circularImageView = (CircularImageView) getActivity
                ().findViewById(R.id.img_drawer_profile_pic);

        circularImageView.setImageBitmap(bm);

        // setting uri path of image into shared preferences
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PROFILE_URI, resultUri.toString());
        editor.apply();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //setPreferencesFromResource(R.xml.preference, rootKey);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    /**
     * onSharedPreferenceChange listener for default shared preferences, this
     * listener changes EditTextPreference UserName and the ListPreference
     * GameMode summary when user has changed a new default value
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case "user_name": {
                String userName = sharedPreferences.getString(key, "NULL");
                EditTextPreference userNamePreference = (EditTextPreference)
                        findPreference("user_name");

                userNamePreference.setSummary(userName);
            }
            case "game_mode": {
                String gameMode = sharedPreferences.getString("game_mode",
                        "NULL");
                ListPreference gameModePreference = (ListPreference)
                        findPreference("game_mode");

                gameModePreference.setSummary(gameMode);
            }
        }
    }
}
