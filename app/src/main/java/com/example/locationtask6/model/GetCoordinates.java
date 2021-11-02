package com.example.locationtask6.model;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.locationtask6.view.App;
import com.example.locationtask6.view.Notifications;
import com.example.locationtask6.view.SnackBarViewClass;
import com.example.locationtask6.view.TrackActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class GetCoordinates {


    private final Subject<ResultClass> locationSubject = PublishSubject.create();
    private final Context context = App.getContext();
    private LocationSettingsRequest locationSettingsRequest;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private LatLng currentLatLng;
    private ResultClass currentPoint;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    public GetCoordinates() {

    }

    public void buildLocationRequest(int interval) {
        Log.v("Order", "Build Location Request");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(interval);
        //locationRequest.setSmallestDisplacement(15);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void buildLocationSettingsRequest() {
        Log.v("Order", "Build Location Settings Request");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addAllLocationRequests(Collections.singleton(locationRequest));
        locationSettingsRequest = builder.build();
    }

    public void buildLocationCallBack() {
        Log.v("Order", "Build Location Callback");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
              //  Log.v("TakeCoordinates", "on Location Result");
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                currentPoint = updateLocation();
                locationSubject.onNext(currentPoint);
            }
        };
    }

    public void startLocationUpdates() {

       // Log.v("TakeCoordinates","Start Location Updates");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(context).checkLocationSettings(locationSettingsRequest);


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {

            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    task.getResult(ApiException.class);
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    Log.v("TakeCoordinates","PERMISSION_GRANTED");
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            Log.v("TakeCoordinates","RESOLUTION_REQUIRED");
                            try {
                                ResolvableApiException
                                        resolvableApiException =
                                        (ResolvableApiException) exception;
                                Log.v("TakeCoordinates","RESOLUTION_REQUIRED"  );
                                Intent intent = new Intent("SHOW_SNACKBAR");
                                intent.putExtra("ApiException", resolvableApiException.getResolution());

                                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,0);
                                pendingIntent.send(context,0,intent);

                            } catch (ClassCastException | PendingIntent.CanceledException e) {
                                // Ignore, should be an impossible error.
                            }

                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.v("TakeCoordinates","SETTINGS_CHANGE_UNAVAILABLE");
                            break;
                    }
                }
            }
        });
    }


    public ResultClass updateLocation() {
        if (currentLocation != null) {
            currentLatLng =
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Log.v("TakeCoordinates", "Taking coordinates. Current location --> " + currentLatLng );
        }
        return new ResultClass(Utils.getCurrentTime(), currentLatLng);
    }

    public void stopLocationUpdates() {
        Log.v("TakeCoordinates", "StopLocationUpdates");
        if (fusedLocationClient!=null){
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
    public Subject<ResultClass> getLocationPoint() {
        return locationSubject;
    }
}
