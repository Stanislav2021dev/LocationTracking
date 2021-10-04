package com.example.locationtask6.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.locationtask6.R;
import com.example.locationtask6.model.GetCoordinates;
import com.example.locationtask6.model.LocationUpdatesBroadcastReceiver;
import com.example.locationtask6.model.BackGroundService;
import com.example.locationtask6.roomdb.CoordinatesDataBase;
import com.example.locationtask6.presenter.TrackPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;


import java.util.List;

import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

public class TrackActivity extends MvpAppCompatActivity implements TrackInterface, OnMapReadyCallback {

    @InjectPresenter
    public TrackPresenter trackPresenter;

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 222;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private CoordinatesDataBase coordinatesDataBase;
    private final LocationUpdatesBroadcastReceiver mReceiver= new LocationUpdatesBroadcastReceiver();
    private GetCoordinates getCoordinates = new GetCoordinates();
    final static Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        trackPresenter.start();
        getLocationPermission(this,trackPresenter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("Service","OnResume" + isAppOnForeground(this));
        //registerReceiver(mReceiver, new IntentFilter("android.intent.action.ACTION_PROCESS_UPDATES"));


}

    @Override
    protected void onStart() {
        Log.v("Service","OnStart" + isAppOnForeground(this));
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("Service","OnStop" + isAppOnForeground(this));
       // registerReceiver(mReceiver,new IntentFilter("android.intent.action.LOCATION_READY"));
        startServ();
       handler.postDelayed(checkAppIsRunning, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // unregisterReceiver(mReceiver);
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

    public void getLocationPermission(Context context,TrackPresenter trackPresenter){
        Log.v("Loc","GetLocationPermission()");
        String [] permissions={FINE_LOCATION};

        if(context.checkSelfPermission(FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
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

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                trackPresenter.startLocationUpdates();

            }
            else {
                showSnackBar("Location permission needed", "Allow", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + getPackageName()));
                            finish();
                            startActivity(intent);
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


    public boolean isAppOnForeground(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
        }
        return true;
    }

      public  Runnable checkAppIsRunning = new Runnable() {
            @Override
            public void run() {

                if (!(isAppOnForeground(LogInActivity.getInstance()))) {
                    startServ();
                    handler.postDelayed(checkAppIsRunning, 5000);
                }
            }
        };

    public void startServ(){

            Intent intent = new Intent();
            PendingIntent pendingIntent =createPendingResult(1,intent,0);

            intent=new Intent(this, BackGroundService.class).putExtra("pi",pendingIntent)
                    .putExtra("getCoord",getCoordinates);
            startService(intent);

        }
}

