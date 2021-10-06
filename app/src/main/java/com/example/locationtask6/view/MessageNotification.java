package com.example.locationtask6.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;

import com.example.locationtask6.R;
import com.example.locationtask6.model.GetCoordinates;
import com.example.locationtask6.model.LocationUpdatesService;
import com.example.locationtask6.model.Utils;
import com.example.locationtask6.presenter.TrackPresenter;


import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

public class MessageNotification {

    private static final String CHANNEL_ID = "channel_01";
    private NotificationManager mNotificationManager;
    private final Context context = LogInActivity.getContext();

    public Notification getNotification() {

        Intent intent = new Intent(context, LocationUpdatesService.class);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String msg = sharedPref.getString("currentLoc", "Not Available");

        PendingIntent servicePendingIntent = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, TrackActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .addAction(R.drawable.ic_baseline_launch_24, "Launch Activity", activityPendingIntent)
                .addAction(R.drawable.ic_outline_cancel_24, "Remove updates", servicePendingIntent)
                .setContentText(msg)
                .setContentTitle(getLocationTitle())
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(msg)
                .setWhen(System.currentTimeMillis());

            builder.setChannelId(CHANNEL_ID);
        return builder.build();
    }

    public String getLocationTitle() {
        return context.getString(R.string.location_updated,
                DateFormat.getDateTimeInstance().format(new Date()));
    }

    public void initNotificationManager(){
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        CharSequence name = context.getString(R.string.app_name);
        NotificationChannel mChannel =
                new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        mNotificationManager.createNotificationChannel(mChannel);
    }

}
