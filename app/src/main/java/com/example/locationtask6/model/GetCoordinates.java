package com.example.locationtask6.model;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.locationtask6.view.App;
import com.example.locationtask6.view.LogInActivity;
import com.example.locationtask6.view.TrackActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class GetCoordinates {

    private static final int CHECK_SETTINGS_CODE = 111;
    private final Subject<ResultClass> locationSubject = PublishSubject.create();
    private final Context context = App.getContext();
    private LocationSettingsRequest locationSettingsRequest;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private Location currentLocation;
    private LatLng currentLatLng;
    private ResultClass currentPoint;

    public GetCoordinates() {

    }

    public void buildLocationRequest(int interval) {
        Log.v("Order", "Build Location Request");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(interval);
        // locationRequest.setSmallestDisplacement(10);
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
                Log.v("Order", "Build Location Callback on Location Result");
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                currentPoint = updateLocation();
                locationSubject.onNext(currentPoint);
            }
        };
    }

    public void startLocationUpdates() {
        Log.v("Order", "StartLocationUpdates()");
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context);
        settingsClient = LocationServices.getSettingsClient(context);

        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(locationSettingsResponse -> {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
                            Looper.myLooper());

                })
                .addOnFailureListener(e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {

                        Intent intent = new Intent(context, TrackActivity.class);

                        ResolvableApiException
                                resolvableApiException =
                                (ResolvableApiException) e;

                        intent.putExtra("ApiExeption", resolvableApiException.getResolution());

                    }
                });
    }

    public ResultClass updateLocation() {

        if (currentLocation != null) {
            currentLatLng =
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Log.v("TakeCoordinates", "Taking coordinates. Current location --> " + currentLatLng);
        }
        return new ResultClass(Utils.getCurrentTime(), currentLatLng);
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public Subject<ResultClass> getLocationPoint() {
        return locationSubject;
    }


}
