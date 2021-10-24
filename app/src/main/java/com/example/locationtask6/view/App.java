package com.example.locationtask6.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;


public class App extends Application {

    private static App instance;
    private Activity mCurrentActivity = null;

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    public static App getInstance() {
        return instance; }

    public static Context getContext(){
        return instance.getApplicationContext();
    }


    @Override public void onCreate(){
        super.onCreate();
        instance = this;
    }


    @Override
    public void onTerminate() {


        super.onTerminate();
    }

}

