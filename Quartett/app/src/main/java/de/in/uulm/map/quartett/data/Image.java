package de.in.uulm.map.quartett.data;

import android.net.Uri;

import com.orm.SugarRecord;

/**
 * Created by alex on 12/17/16.
 */

public class Image extends SugarRecord {

    public String mUri;
    public String mDescription;

    public Image() {

    }

    public Image(String mUri, String mDescription) {

        this.mUri = mUri;
        this.mDescription = mDescription;
    }
}
