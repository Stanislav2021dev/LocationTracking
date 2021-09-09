package com.example.locationtask6.model;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import com.example.locationtask6.presenter.TrackPresenter;
import com.example.locationtask6.roomdb.CoordinatesDataBase;
import com.example.locationtask6.roomdb.CoordinatesModel;
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
import com.google.android.gms.tasks.OnFailureListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import static io.reactivex.rxjava3.subjects.PublishSubject.create;

public class GetCoordinates {

    private LocationSettingsRequest locationSettingsRequest;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private Location currentLocation;
    private  LatLng currentLatLng;
    private static final int CHECK_SETTINGS_CODE = 111;
    private String currentDateTime;


    public void startLocationUpdates() {
        Log.v("Loc","StartLocationUpdates()");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(TrackActivity.getInstance());
        settingsClient = LocationServices.getSettingsClient(TrackActivity.getInstance());

        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(TrackActivity.getInstance(), locationSettingsResponse -> {
                    if (ActivityCompat.checkSelfPermission(TrackActivity.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,
                            Looper.myLooper()
                    );

                })

                .addOnFailureListener(TrackActivity.getInstance(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        int statusCode = ((ApiException) e).getStatusCode();
                        if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                ResolvableApiException
                                        resolvableApiException =
                                        (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(TrackActivity.getInstance(), CHECK_SETTINGS_CODE);
                            } catch (IntentSender.SendIntentException sie) {
                                sie.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void buildLocationCallBack(Subject<ResultClass> locationPoint) {
        Log.v("Loc","BuildLocationCallback()");
        locationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Log.v("Loc","OnLocationResult()");
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                ResultClass resultClass =  updateLocation();
                locationPoint.onNext(resultClass);

            }
        };
    }

    public ResultClass updateLocation() {
        Log.v("Loc","UpdateLocation()");
        Log.v("Loc","CurrentTread Update " + Thread.currentThread().getName());
        if (currentLocation != null) {
            currentLatLng =
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Log.v("Loc", "CoordinatesModel " + currentLatLng);
            currentDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        }
        return new ResultClass(currentDateTime,currentLatLng);
    }

    public void buildLocationRequest() {
        Log.v("Loc","buildLocationRequest()");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        //  locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void buildLocationSettingsRequest() {
        Log.v("Loc","buildLocationSettingsRequest()");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addAllLocationRequests(Collections.singleton(locationRequest));
        locationSettingsRequest = builder.build();
    }
}
