package com.example.locationtask6.model;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.locationtask6.R;
import com.example.locationtask6.view.App;
import com.example.locationtask6.view.Notifications;
import com.example.locationtask6.view.TrackActivity;
import com.google.type.DateTime;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.Subject;

import static android.location.LocationManager.PROVIDERS_CHANGED_ACTION;

public class LocationUpdatesService extends Service {

    private static final int NOTIFICATION_ID = 123;

    private final int BACKGROUND_GATHERING_COORDINATES_TIME_INTERVAL=60*1000;
    private Subject<ResultClass> locationPoint;
    private GetCoordinates getCoordinates;
    private InitWorkManager initWorkManager;
    private Notifications notifications;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public LocationUpdatesService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("TakeCoordinates", "onCreate Service");
        initWorkManager = new InitWorkManager();
        getCoordinates = new GetCoordinates();
        notifications=new Notifications();
        locationPoint= getCoordinates.getLocationPoint();
        locationPoint.subscribe(resultClassObserver());

    }

    @Override
    public void onDestroy() {
        Log.v("TakeCoordinates", "onDestroy Service");
        getCoordinates.stopLocationUpdates();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("TakeCoordinates", "Service onStartCommand");


        getCoordinates.buildLocationRequest(BACKGROUND_GATHERING_COORDINATES_TIME_INTERVAL);
        getCoordinates.buildLocationSettingsRequest();
        getCoordinates.buildLocationCallBack();
        getCoordinates.startLocationUpdates();

        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("TakeCoordinates", "On unbind");
        return super.onUnbind(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public IBinder onBind(Intent intent) {
        Log.v("TakeCoordinates", "On bind");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public io.reactivex.rxjava3.core.Observer<ResultClass> resultClassObserver() {
        return new Observer<ResultClass>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                executorService.execute(() -> LoadData.initDb());
            }

            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResultClass resultClass) {
                initWorkManager.initWork(resultClass);
                startForeground(NOTIFICATION_ID, notifications.locationInfoNotification(resultClass.getCurrentLocation().toString()));
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.v("TakeCoordinates","Error");

            }

            @Override
            public void onComplete() {

            }
        };
    }
}
