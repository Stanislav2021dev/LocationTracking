package com.example.locationtask6.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.locationtask6.presenter.TrackPresenter;
import com.example.locationtask6.view.LogInActivity;
import com.example.locationtask6.view.TrackActivity;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BackGroundService extends Service {

    private final String LOG_TAG = "Service";
    Context context;
    private TrackActivity trackActivity;
    private TrackPresenter trackPresenter;
    private LocationManager locationManager;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "OnCreate Service");

        context = this;
        context = LogInActivity.getContext();

        locationManager =
                (LocationManager) context.getApplicationContext().getSystemService(LOCATION_SERVICE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "OnDestroy Service");
    }


    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOG_TAG, "OnStart command");




        String provider = LocationManager.GPS_PROVIDER;
        int time = 6000;
        int distance = 10;

        intent = new Intent(context, LocationUpdatesBroadcastReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, START_STICKY);

        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        locationManager.requestLocationUpdates(provider, time, distance, pendingIntent);

        if (LocationResult.hasResult(intent)) {
            LocationResult result = LocationResult.extractResult(intent);
            if (result != null) {
                List<Location> locations = result.getLocations();
                Location lastLocation = result.getLastLocation();

                Log.v("BackGround", "Lat " + lastLocation.getLatitude() + "Lng " + lastLocation.getLongitude() + "Time ");
            }
        }

        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}