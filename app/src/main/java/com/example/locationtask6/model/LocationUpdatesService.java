package com.example.locationtask6.model;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.locationtask6.R;
import com.example.locationtask6.view.TrackActivity;
import com.google.type.DateTime;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.Subject;

public class LocationUpdatesService extends Service {
    private static final int NOTIFICATION_ID = 123;
    private final String NOTIFICATION_CHANNEL_ID = "com.example.locationtask6.model";
    private final String CHANNEL_NAME = "My Background Service";
    private final String NOTIFICATION_MSG="Your location at ";
    private final String NOTIFICATION_BTN_TEXT="Launch Application";
    private final int BACKGROUND_GATHERING_COORDINATES_TIME_INTERVAL=60*1000;
    private Subject<ResultClass> locationPoint;
    private GetCoordinates getCoordinates;
    private InitWorkManager initWorkManager;

    public LocationUpdatesService() {
    }

    @Override
    public void onDestroy() {
        Log.v("TakeCoordinates", "onDestroy Service");
        getCoordinates.stopLocationUpdates();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v("TakeCoordinates", "Service onStartCommand");
        initWorkManager = new InitWorkManager();
        getCoordinates = new GetCoordinates();
        locationPoint= getCoordinates.getLocationPoint();
        locationPoint.subscribe(resultClassObserver());
        getCoordinates.buildLocationRequest(BACKGROUND_GATHERING_COORDINATES_TIME_INTERVAL);
        getCoordinates.buildLocationSettingsRequest();
        getCoordinates.buildLocationCallBack();
        getCoordinates.startLocationUpdates();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public io.reactivex.rxjava3.core.Observer<ResultClass> resultClassObserver() {
        return new Observer<ResultClass>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
            }

            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResultClass resultClass) {
                initWorkManager.initWork(resultClass);
                startForeground(NOTIFICATION_ID, createNotification(resultClass.getCurrentLocation().toString()));
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    private Notification createNotification(@NonNull String msg){

        createChannel();

        PendingIntent activityPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), TrackActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);

        DateFormat df = new SimpleDateFormat("HH:mm:ss MM.dd");
        String time = df.format(Calendar.getInstance().getTime());

        return new NotificationCompat.Builder(getApplicationContext(),NOTIFICATION_CHANNEL_ID)
                .addAction(R.drawable.ic_baseline_launch_24, NOTIFICATION_BTN_TEXT, activityPendingIntent)
                .setContentText(msg)
                .setContentTitle(NOTIFICATION_MSG+time)
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .build();
    }

    private void createChannel() {
        NotificationChannel chan =
                new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(chan);
    }
}