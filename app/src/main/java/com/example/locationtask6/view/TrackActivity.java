package com.example.locationtask6.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.locationtask6.R;
import com.example.locationtask6.model.LocationUpdatesService;
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

public class TrackActivity extends MvpAppCompatActivity implements TrackInterface, OnMapReadyCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    static TrackActivity trackActivity;
    @InjectPresenter
    public TrackPresenter trackPresenter;

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 222;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private CoordinatesDataBase coordinatesDataBase;

   // private GetCoordinates getCoordinates = new GetCoordinates();
    final static Handler handler = new Handler();

    private static final String TAG = TrackActivity.class.getSimpleName();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
   // private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            LocationUpdatesService.LocalBinder binder =
                    (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            Log.i("Connected", "onServiceConnected" + mService);
            mBound = true;
            startService(new Intent(getApplicationContext(), LocationUpdatesService.class));
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trackActivity=this;
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        trackPresenter.start();
        getLocationPermission(this, trackPresenter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Connected", "onStart");

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);



    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("Service", "OnResume");
    }


    @Override
    protected void onPause() {
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();


        Log.v("Service", "OnStop");
        // registerReceiver(mReceiver,new IntentFilter("android.intent.action.LOCATION_READY"));
        //startServ();
        // handler.postDelayed(checkAppIsRunning, 5000);
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

    public void getLocationPermission(Context context, TrackPresenter trackPresenter) {
        Log.v("Loc", "GetLocationPermission()");
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
        Log.v("Loc", "OnRequestPermission()");

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                trackPresenter.startLocationUpdates();

            } else {
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

    public  TrackPresenter getTrackPresenter(){
        return trackPresenter;
    }

    public static TrackActivity getInstance() {
        return trackActivity; }
/*
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

 */
/*
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

*/


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


    }
}

/*

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
              //  Toast.makeText(TrackActivity.this,Utils.getLocationText(location),Toast.LENGTH_SHORT).show();

            }
        }



}

}

 */