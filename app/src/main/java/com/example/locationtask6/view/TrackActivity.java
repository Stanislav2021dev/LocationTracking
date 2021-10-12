package com.example.locationtask6.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.locationtask6.R;
import com.example.locationtask6.model.GetCoordinates;
import com.example.locationtask6.model.LongWorker;
import com.example.locationtask6.presenter.TrackPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.TimeUnit;

import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

public class TrackActivity extends MvpAppCompatActivity implements TrackInterface, OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 222;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private GetCoordinates getCoordinates = new GetCoordinates();


    @InjectPresenter
    public TrackPresenter trackPresenter;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    protected void onResume() {
        Log.v("TakeCoordinates", "OnResume");
        trackPresenter.start();
        getLocationPermission(this, trackPresenter);
        super.onResume();

    }


    @Override
    protected void onStop() {
        Log.v("TakeCoordinates", "OnStop");
        trackPresenter.getGetCoordinates().stopLocationUpdates();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v("TakeCoordinates", "OnDestroy");
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng kyiv = new LatLng(50.45, 30.55);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kyiv));
        if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    public void getLocationPermission(Context context, TrackPresenter trackPresenter) {
        Log.v("Order", "GetLocationPermission()");
        String[] permissions = {FINE_LOCATION};
        if (context.checkSelfPermission(FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            trackPresenter.startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v("Order", "OnRequestPermission()");

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                trackPresenter.startLocationUpdates();

            } else {
                showSnackBar("Location permission needed", "Allow", v -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + getPackageName()));
                    finish();
                    startActivity(intent);
                });
            }
        }
    }

    @Override
    public void addPoint(LatLng currentLatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current location"));
    }

    @Override
    public void showSnackBar(String mainText, String action, View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content), mainText, Snackbar.LENGTH_INDEFINITE)
                .setAction(action, listener).show();
    }

}
