package com.example.locationtask6.view;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.work.WorkManager;

import com.example.locationtask6.model.LocationUpdatesService;


public class App extends Application {

    private static App instance;
    private Intent mIntent;
    public static App getInstance() {
        return instance; }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

    @Override public void onCreate(){
        super.onCreate();
        instance = this;
        WorkManager.getInstance(this).cancelAllWorkByTag("BackgroundWork");
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new LifecycleEventObserver() {

           // final String workName = "BackgroundWork";

            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_DESTROY) {
                   // WorkManager.getInstance(App.this).cancelUniqueWork(workName);

                    if (mIntent!=null){
                        stopService(mIntent);
                    }


                } else if (event == Lifecycle.Event.ON_STOP) {

                    mIntent = new Intent(getApplicationContext(), LocationUpdatesService.class);
                    startService(mIntent);

                  //  PeriodicWorkRequest request = new PeriodicWorkRequest
                   //         .Builder(LongWorker.class, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                    //        .build();

                //    WorkManager.getInstance(App.this)
                   //         .enqueueUniquePeriodicWork(workName, ExistingPeriodicWorkPolicy.REPLACE, request);
                }
            }
        });
    }

}

