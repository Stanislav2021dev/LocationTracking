package com.example.locationtask6.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.locationtask6.R;
import com.example.locationtask6.model.LocationUpdatesService;
import com.example.locationtask6.model.LocationSettingsChangeReciver;
import com.example.locationtask6.model.Utils;
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

import static android.location.LocationManager.PROVIDERS_CHANGED_ACTION;

public class TrackActivity extends MvpAppCompatActivity implements TrackInterface, OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 222;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private Intent mIntent;
    public static LocationSettingsChangeReciver mReceiver;
    private SnackBarViewClass snackbar = new SnackBarViewClass();
    private BroadcastReceiver apiExeptionReceiver;
    private Snackbar errorSnackBar;
    private boolean firstCreate= true;

    @InjectPresenter
    public TrackPresenter trackPresenter;
    private GoogleMap mMap;

    public TrackActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        mIntent = new Intent(getApplicationContext(), LocationUpdatesService.class);
    }


    @Override
    protected void onResume() {
        hideSnackBar();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        Log.v("TakeCoordinates", "OnStart");
        if (mIntent != null) {
            Log.v("TakeCoordinates", "Stop Service");
            stopService(mIntent);
        }
        trackPresenter.start();
        getLocationPermission();
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.v("TakeCoordinates", "OnStop");
        trackPresenter.getGetCoordinates().stopLocationUpdates();
        startService(mIntent);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        trackPresenter.getGetCoordinates().stopLocationUpdates();
        if (mIntent != null) {
            Log.v("TakeCoordinates", "Stop Service");
            stopService(mIntent);
        }
        Log.v("TakeCoordinates", "Destroy TrackActivity");
        unregisterReceiver(mReceiver);
        unregisterReceiver(apiExeptionReceiver);
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

    public void getLocationPermission() {
        String[] permissions = {FINE_LOCATION};
        if (App.getContext().checkSelfPermission(FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            registerBroadcastReceivers();
            trackPresenter.startLocationUpdates();

        } else {
            ActivityCompat.requestPermissions(this,
                    permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                registerBroadcastReceivers();
                trackPresenter.startLocationUpdates();

            } else {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                snackbar.createSnackBar(this, "Location permission needed", "Allow permission",
                        intent);
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

    public void createApiExeptionReceiver() {
        apiExeptionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v("TakeCoordinates","ApiExeprion Receiver");
                    hideSnackBar();
                 if (Utils.isAppOnForeground(context) && !Utils.isGpsEnabled()) {
                    Intent
                            turnOnLocationIntent =
                            new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     errorSnackBar =
                            snackbar.createSnackBar(context , "Turn On Location", "Ok", turnOnLocationIntent);

                }
            }
        };
    }
    public void hideSnackBar(){
        if (Utils.isGpsEnabled() && errorSnackBar!=null && errorSnackBar.isShown()) {
            errorSnackBar.dismiss();
        }
    }
    public void registerBroadcastReceivers() {
        if (firstCreate) {
            mReceiver = new LocationSettingsChangeReciver();
            IntentFilter filter = new IntentFilter(PROVIDERS_CHANGED_ACTION);
            registerReceiver(mReceiver, filter, 0);
            createApiExeptionReceiver();
            registerReceiver(apiExeptionReceiver, new IntentFilter("SHOW_SNACKBAR"), 0);
            firstCreate=false;
        }
    }
}
