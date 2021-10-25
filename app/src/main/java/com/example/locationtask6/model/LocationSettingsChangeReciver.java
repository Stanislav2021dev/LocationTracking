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

import java.util.List;

import io.reactivex.functions.Action;

import static android.content.Context.NOTIFICATION_SERVICE;

public class LocationSettingsChangeReciver extends BroadcastReceiver {

    Notifications notifications = new Notifications();
    SnackBarViewClass snackBar=new SnackBarViewClass();
    Intent serviceIntent = new Intent(App.getContext(), LocationUpdatesService.class);

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("TakeCoordinates", "Receive Broadcast");



        if (!Utils.isGpsEnabled() && Utils.isAppOnForeground(context)) {
            Intent turnOnLocationIntent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            snackBar.createSnackBar(context,"Turn on Location!", "Ok",turnOnLocationIntent);
        }

        else if (!Utils.isGpsEnabled() && !Utils.isAppOnForeground(context)) {
            notifications.errorNotification();
          //  context.stopService(serviceIntent);
        }

        else if (Utils.isGpsEnabled() && !Utils.isAppOnForeground(context)) {
            context.startService(serviceIntent);
             NotificationManager notificationManager =
                   (NotificationManager) App.getContext().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
    }
}
                //   Intent startActivityIntent = new Intent(App.getContext(), TrackActivity.class);
                //    startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //   App.getContext().startActivity(startActivityIntent);






