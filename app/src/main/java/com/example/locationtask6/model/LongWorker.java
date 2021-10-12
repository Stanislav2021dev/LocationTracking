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
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
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
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

public class LongWorker extends Worker {

    private static final int NOTIFICATION_ID = 123;
    private final String NOTIFICATION_CHANNEL_ID = "com.example.locationtask6.model";
    private final String CHANNEL_NAME = "My Background Service";


    private final ExecutorService executorService = Executors.newSingleThreadExecutor();



    public LongWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @NonNull
    @Override
    public Result doWork() {

        Log.v("TakeCoordinates", "LongWorker doWork() ");
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                setForegroundAsync(createNotification("Gathering location data...")).get();

                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        if (!executorService.isShutdown()) {
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    uploadCoordinates(new ResultClass(new Date(), new LatLng(location.getLatitude(), location.getLongitude())));
                                }
                            });
                        }
                    }
                };
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener, Looper.getMainLooper());

                while (executorService.awaitTermination(1, TimeUnit.DAYS));

                locationManager.removeUpdates(locationListener);

                return Result.success();
            }
            catch (InterruptedException | ExecutionException e) {
                return Result.failure();
            }
        }
        else {
            return Result.failure();
        }
    }

    @Override
    public void onStopped() {
        super.onStopped();
        executorService.shutdownNow();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    private ForegroundInfo createNotification(@NonNull String msg){

            createChannel();

            String cancel = "Cancel";

           PendingIntent intent = WorkManager.getInstance(getApplicationContext())
                .createCancelPendingIntent(getId());

            PendingIntent activityPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), TrackActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);

            Notification notification = new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL_ID)
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
            return new ForegroundInfo(NOTIFICATION_ID,notification);
        }

    private void createChannel() {
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
    }

    private void uploadCoordinates(ResultClass result){
        boolean loadSuccess = LoadData.uploadToFireBase(result);
        if (!loadSuccess || !Utils.isOnline()) {
            LoadData.uploadToRoomDb(result);
        }
        LoadData.uploadFromDbToFb();
    }
}
