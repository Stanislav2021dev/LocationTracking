package com.example.locationtask6.model;

import android.content.Context;
import android.icu.text.UnicodeSetIterator;
import android.location.Location;
import android.preference.PreferenceManager;

import com.example.locationtask6.R;

import java.text.DateFormat;
import java.util.Date;

public class Utils {
    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";


    static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

}
