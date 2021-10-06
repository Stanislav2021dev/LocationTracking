package com.example.locationtask6.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.room.Database;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.locationtask6.view.LogInActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;

import io.reactivex.rxjava3.subjects.Subject;

public class BackGroundWorkManager extends Worker {
    private LatLng currentLatLng;
    private String currentDateTime;
    private String latitude;
    private String longitude;
    private Data output;



    public BackGroundWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.v("WorkRes", "do Work");

        LocationManager locationManager =
                (LocationManager) LogInActivity.getContext().getSystemService(Context.LOCATION_SERVICE);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityCompat.checkSelfPermission(LogInActivity.getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        currentLatLng =
                                new LatLng(location.getLatitude(), location.getLongitude());
                        locationManager.removeUpdates(this);
                        Log.v("WorkRes", "workmanager" + location.getLatitude() +" "+ location.getLongitude());

                        currentDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                            output = new Data.Builder()
                                    .putString("latitude", String.valueOf(location.getLatitude()))
                                    .putString("longitude", String.valueOf(location.getLongitude()))
                                    .build();
                    }
                });
            }
        },1000);

        return Result.success(output);

    }


}
