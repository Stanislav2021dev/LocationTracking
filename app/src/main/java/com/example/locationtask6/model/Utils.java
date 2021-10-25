package com.example.locationtask6.model;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.example.locationtask6.view.App;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Utils {


    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(Calendar.getInstance().getTime());
    }

    public static boolean isOnline() {
        ConnectivityManager
                connectivityManager =
                App.getContext().getSystemService(ConnectivityManager.class);
        Network currentNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(currentNetwork);
        return currentNetwork != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

    }

    public static boolean isAppOnForeground(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(mContext.getPackageName())) {
                return false;
            }
        }
        return true;
    }

    public static boolean  isGpsEnabled(){
        LocationManager
                locationManager =
                (android.location.LocationManager) App.getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


}
