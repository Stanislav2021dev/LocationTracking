package com.example.locationtask6.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.locationtask6.model.LoadData;
import com.example.locationtask6.model.LongWorker;

import java.util.concurrent.TimeUnit;


public class App extends Application {

    private static App instance;
    public static App getInstance() {
        return instance; }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override public void onCreate(){
        super.onCreate();
        instance = this;

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleEventObserver() {

            final String workName = "BackgroundWork";

            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_START) {
                    WorkManager.getInstance(App.this).cancelUniqueWork(workName);
                } else if (event == Lifecycle.Event.ON_STOP) {
                    PeriodicWorkRequest request = new PeriodicWorkRequest
                            .Builder(LongWorker.class, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                            .build();

                    WorkManager.getInstance(App.this)
                            .enqueueUniquePeriodicWork(workName, ExistingPeriodicWorkPolicy.REPLACE, request);
                }
            }
        });
    }
}

