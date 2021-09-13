package com.example.locationtask6.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
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
import androidx.core.content.ContextCompat;

import com.example.locationtask6.R;
import com.example.locationtask6.roomdb.CoordinatesDataBase;
import com.example.locationtask6.presenter.TrackPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;


import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

public class TrackActivity extends MvpAppCompatActivity implements TrackInterface, OnMapReadyCallback {


    @InjectPresenter
    public TrackPresenter trackPresenter;

    private GoogleMap mMap;
    static TrackActivity trackActivity;
    private static Context context;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 222;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private boolean mLocationPermissionGranted;
    private CoordinatesDataBase coordinatesDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        trackActivity = this;
        TrackActivity.context = getApplicationContext();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        trackPresenter.start();
        getLocationPermission();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng kyiv = new LatLng(50.45, 30.55);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kyiv));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    public void getLocationPermission(){
        Log.v("Loc","GetLocationPermission()");
        String [] permissions={FINE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted=true;
            trackPresenter.startLocationUpdates();
        }
        else {
            ActivityCompat.requestPermissions(this,
                    permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v("Loc","OnRequestPermission()");
        mLocationPermissionGranted=false;
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                trackPresenter.startLocationUpdates();

            }
            else {
                showSnackBar("Location permission needed", "Allow", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + getPackageName()));
                            finish();
                            startActivity(i);
                            }
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

    public static TrackActivity getInstance() {
        return trackActivity; }


}
