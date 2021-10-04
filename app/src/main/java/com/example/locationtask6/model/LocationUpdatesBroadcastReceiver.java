package com.example.locationtask6.model;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.example.locationtask6.view.TrackActivity;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.installations.Utils;

import java.util.List;

import io.reactivex.rxjava3.subjects.Subject;


public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {

    GetCoordinates getCoordinates = new GetCoordinates();
    private Location currentLocation;
    private Location location;

    static final String ACTION_PROCESS_UPDATES =
            "com.example.locationtask6.model.action" +
                    ".PROCESS_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("BackGround", "onReceive");

        String key = LocationManager.KEY_LOCATION_CHANGED;
        location = (Location) intent.getExtras().get(key);

        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            long time = location.getTime();

        Log.v("BackGround", "Lat "+ lat +"Lng " + lng + "Time " + time);
        }
     }
}