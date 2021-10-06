package com.example.locationtask6.model;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ServiceLifecycleDispatcher;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.locationtask6.presenter.TrackPresenter;
import com.example.locationtask6.view.MessageNotification;

import java.util.concurrent.TimeUnit;


public class LocationUpdatesService extends Service implements LifecycleOwner {

    private static final String PACKAGE_NAME =
            "com.example.locationtask6.model";

    private static final String TAG = "Connected";

    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    private static final int NOTIFICATION_ID = 12345678;
    private final IBinder mBinder = new LocalBinder();
    private boolean mChangingConfiguration = false;
    private Handler mServiceHandler;
    private MessageNotification messageNotification;
    private String latitude;
    private String longitude;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Connected", "Start service OnCreate");

        messageNotification = new MessageNotification();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();

        mServiceHandler = new Handler(handlerThread.getLooper());
        messageNotification.initNotificationManager();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v("WorkRes", "On Start Command");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        startForeground(NOTIFICATION_ID, messageNotification.getNotification());


        PeriodicWorkRequest request = new PeriodicWorkRequest
                .Builder(BackGroundWorkManager.class, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this)
                .enqueue(request);

        WorkManager.getInstance(this)
                .getWorkInfoByIdLiveData(request.getId()).observe(this, workInfo -> {
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                latitude = workInfo.getOutputData().getString("latitude");
                longitude = workInfo.getOutputData().getString("longitude");
                Log.v("WorkRes", "Result" + latitude + longitude);
            }
        });

        if (startedFromNotification) {
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.v("WorkRes", "On destroy");
        super.onDestroy();
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        Log.v("WorkRes", "Last client unbound from service");
        //   if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
        Log.v("WorkRes", "Starting foreground service" + mChangingConfiguration + " " + Utils.requestingLocationUpdates(this));
        return true;
    }


    @Override
    public void onRebind(Intent intent) {
        Log.v("WorkRes", "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v("WorkRes", "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    public class LocalBinder extends Binder {
        public LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }
}

