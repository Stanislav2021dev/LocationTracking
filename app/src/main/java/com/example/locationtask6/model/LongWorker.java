package com.example.locationtask6.model;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.locationtask6.R;
import com.example.locationtask6.view.App;
import com.example.locationtask6.view.LogInActivity;
import com.example.locationtask6.view.TrackActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

public class LongWorker extends Worker {

    private static final String CHANNEL_ID = "channel_01";
    private static final int NOTIFICATION_ID = 123;
    private NotificationManager mNotificationManager;
    private Context context;
    private String latitude;
    private String longitude;
    private final String NOTIFICATION_CHANNEL_ID = "com.example.locationtask6.model";
    private final String CHANNEL_NAME = "My Background Service";
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLocation;



    public LongWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context=context;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    @Override
    public Result doWork() {

        Log.v("WorkRes", "do Work Long Worker");
        LocationManager locationManager =
                (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            ArrayBlockingQueue<Location> locations = new ArrayBlockingQueue<>(1);

            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, location -> {

                locations.offer(location);
                latitude=String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());

            }, Looper.getMainLooper());

            try {
                Location location = locations.take();

                LatLng currentLatLng =
                        new LatLng(location.getLatitude(), location.getLongitude());

                uploadCoordinates(new ResultClass(Utils.getCurrentTime(),currentLatLng));

                Log.v("WorkRes", "do Work  coordinates result " + location.getLatitude() + location.getLongitude());

                setForegroundAsync(createNotification(latitude+longitude));

                return Result.success();
            }
            catch (InterruptedException e) {
                return Result.failure();
            }
        }
        else {
            return Result.failure();
        }
}

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    private ForegroundInfo createNotification(@NonNull String progress){

            createChannel();

            String msg = latitude+ " "+longitude;
            String cancel = "Cancel";

           PendingIntent intent = WorkManager.getInstance(context)
                .createCancelPendingIntent(getId());

            PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, TrackActivity.class), 0);

            Notification notification = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
                    .addAction(R.drawable.ic_baseline_launch_24, "Launch Activity", activityPendingIntent)
                    .addAction(android.R.drawable.ic_delete, cancel, intent)
                    .setContentText(msg)
                    .setContentTitle(Utils.getCurrentTime())
                    .setOngoing(true)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(msg)
                    .setWhen(System.currentTimeMillis())
                    .build();
            return new ForegroundInfo(NOTIFICATION_ID,notification,FOREGROUND_SERVICE_TYPE_LOCATION);
        }

    private void createChannel() {
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
    }

    private void uploadCoordinates(ResultClass result){
        boolean loadSucess = LoadData.uploadToFireBase(result);
        Log.v("WorkRes","succes " + loadSucess);
        if (!loadSucess || !Utils.isOnline()) {
            LoadData.uploadToRoomDb(result);
        }
        LoadData.uploadFromDbToFb();
    }
}



