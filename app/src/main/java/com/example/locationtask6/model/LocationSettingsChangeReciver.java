package com.example.locationtask6.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.locationtask6.view.App;
import com.example.locationtask6.view.Notifications;
import com.example.locationtask6.view.SnackBarViewClass;
import com.example.locationtask6.view.TrackActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.functions.Action;

import static android.content.Context.NOTIFICATION_SERVICE;

public class LocationSettingsChangeReciver extends BroadcastReceiver {

    Notifications notifications = new Notifications();
    SnackBarViewClass snackBar = new SnackBarViewClass();
    Intent serviceIntent = new Intent(App.getContext(), LocationUpdatesService.class);

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("TakeCoordinates", "Receive broadcast " + "Action " + intent.getAction() + " Flags " + intent.getFlags()
                + " Extras " + intent.getExtras());

        Bundle bundle = intent.getExtras();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            Log.v("TakeCoordinates", key + "=" + bundle.get(key));


            if (bundle.get(key).equals("gps")) {
                Log.v("TakeCoordinates", "Filtered broadcast " + "Action " + intent.getAction() + " Flags " + intent.getFlags()
                        + " Extras " + intent.getExtras());

                if (Utils.isAppOnForeground(context)) {
                    Intent showSnackbarIntent = new Intent("SHOW_SNACKBAR");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,showSnackbarIntent,0);
                    try {
                        pendingIntent.send(context,0,showSnackbarIntent);
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }


                } else if (!Utils.isGpsEnabled() && !Utils.isAppOnForeground(context)) {
                    notifications.errorNotification();
                    //  context.stopService(serviceIntent);
                } else if (Utils.isGpsEnabled() && !Utils.isAppOnForeground(context)) {
                    context.startService(serviceIntent);
                    NotificationManager notificationManager =
                            (NotificationManager) App.getContext().getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                }
            }
        }
    }
}




