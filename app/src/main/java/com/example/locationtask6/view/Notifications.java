package com.example.locationtask6.view;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.locationtask6.R;
import com.example.locationtask6.model.FinishAppReceiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Notifications {


    private final String NOTIFICATION_CHANNEL_ID = "com.example.locationtask6.model";
    private final String CHANNEL_NAME = "My Background Service";
    private final String NOTIFICATION_MSG="Your location at ";
    private final String NOTIFICATION_BTN_TEXT="Launch Application";
    private NotificationManager notificationManager;



    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    public Notification locationInfoNotification(@NonNull String msg){

        createChannel();

        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(App.getContext(), 0,
                new Intent(App.getContext(), TrackActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);

        Intent intent = new Intent("ACTION_FINISH");

        FinishAppReceiver finishAppReceiver = new FinishAppReceiver();
        IntentFilter filter = new IntentFilter("ACTION_FINISH");
        App.getContext().registerReceiver(finishAppReceiver,filter,0);

        PendingIntent closeAppPendingIntent= PendingIntent.getBroadcast(App.getContext(),0,intent,0);


        DateFormat df = new SimpleDateFormat("HH:mm:ss MM.dd");
        String time = df.format(Calendar.getInstance().getTime());

        return new NotificationCompat.Builder(App.getContext(),NOTIFICATION_CHANNEL_ID)
                .addAction(R.mipmap.ic_launcher_round, NOTIFICATION_BTN_TEXT, startActivityPendingIntent)
                .addAction(R.mipmap.ic_launcher_round,"Close App",closeAppPendingIntent)
                .setContentText(msg)
                .setContentTitle(NOTIFICATION_MSG+time)
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_baseline_launch_24)
                .build();
    }


    public  void errorNotification(){
        createChannel();

        PendingIntent turnOnLocationSettingsPendingIntent = PendingIntent.getActivity(App.getContext(),0,
                new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),0 );

        Notification notification =  new NotificationCompat.Builder(App.getContext(), NOTIFICATION_CHANNEL_ID)
                .addAction(R.mipmap.ic_launcher_round,"Turn On Location Settings", turnOnLocationSettingsPendingIntent)
                .setContentText("Error permission needed")
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_baseline_error_24)
                .build();

        notificationManager.notify(111,notification);

    }

    private void createChannel() {
        NotificationChannel chan =
                new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        chan.setShowBadge(true);
        chan.enableLights(true);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager =
                (NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(chan);
    }

}
