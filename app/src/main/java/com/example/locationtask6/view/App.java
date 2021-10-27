package com.example.locationtask6.view;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.locationtask6.model.LocationSettingsChangeReciver;

import static android.location.LocationManager.PROVIDERS_CHANGED_ACTION;


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

    }

}

