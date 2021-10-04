package com.example.locationtask6.model;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

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
import com.google.android.gms.tasks.OnFailureListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import static android.content.Context.LOCATION_SERVICE;
import static io.reactivex.rxjava3.subjects.PublishSubject.create;

public class GetCoordinates implements Parcelable {

    private LocationSettingsRequest locationSettingsRequest;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private Location currentLocation;
    private LatLng currentLatLng;
    private static final int CHECK_SETTINGS_CODE = 111;
    private String currentDateTime;
    private final Subject<ResultClass> locationSubject = PublishSubject.create();
    private LocationManager locationManager;
    private final Context context = LogInActivity.getInstance();
    private ResultClass currentPoint;

    public GetCoordinates() {

    }

    protected GetCoordinates(Parcel in) {
        locationSettingsRequest = in.readParcelable(LocationSettingsRequest.class.getClassLoader());
        locationRequest = in.readParcelable(LocationRequest.class.getClassLoader());
        currentLocation = in.readParcelable(Location.class.getClassLoader());
        currentLatLng = in.readParcelable(LatLng.class.getClassLoader());
        currentDateTime = in.readString();
    }

    public static final Creator<GetCoordinates> CREATOR = new Creator<GetCoordinates>() {
        @Override
        public GetCoordinates createFromParcel(Parcel in) {
            return new GetCoordinates(in);
        }

        @Override
        public GetCoordinates[] newArray(int size) {
            return new GetCoordinates[size];
        }
    };

    public void buildLocationRequest() {
        Log.v("Order", "Build Location Request");
        locationManager =
                (LocationManager) context.getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(LogInActivity.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        Log.v("Order", "locationListener");
                        buildLocationCallBack();
                    }
                });


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
         Log.v("Order","StartLocationUpdates()");
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
                   // fusedLocationClient.requestLocationUpdates(locationRequest,getPendingIntent());
                })

                .addOnFailureListener(LogInActivity.getInstance(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        int statusCode = ((ApiException) e).getStatusCode();



                        if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                            try {
                                ResolvableApiException
                                        resolvableApiException =
                                        (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(LogInActivity.getInstance(), CHECK_SETTINGS_CODE);
                            } catch (IntentSender.SendIntentException sie) {
                                sie.printStackTrace();
                            }
                        }
                    }
                });
    }

    public ResultClass updateLocation() {
        Log.v("Order", "updateLocation");

        if (currentLocation != null) {

            currentLatLng =
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            Log.v("Location", "CoordinatesModel " + currentLatLng);
            currentDateTime =
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        }
        currentPoint=new ResultClass(currentDateTime, currentLatLng);
        return new ResultClass(currentDateTime, currentLatLng);
    }

    public PendingIntent getPendingIntent() {

        Log.v("Order", "getPendingIntent");

        Intent intent = new Intent(context, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    public Subject<ResultClass> getLocationPoint() {
        return locationSubject;
    }
    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(locationSettingsRequest, flags);
        dest.writeParcelable(locationRequest, flags);
        dest.writeParcelable(currentLocation, flags);
        dest.writeParcelable(currentLatLng, flags);
        dest.writeString(currentDateTime);
    }

    public ResultClass getCurrentPoint (){
        return currentPoint;
    }
}
