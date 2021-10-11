package com.example.locationtask6.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.work.WorkManager;


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

