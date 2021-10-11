package com.example.locationtask6.model;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.example.locationtask6.view.App;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
}
