package com.example.locationtask6.model;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.locationtask6.view.App;

import java.util.List;

public class FinishAppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("TakeCoordinates","Finish Receiver");
        App.getContext().unregisterReceiver(this);

            ActivityManager am = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.AppTask> taskInfo = am.getAppTasks();

            if (taskInfo.size()!=0){
                for (ActivityManager.AppTask task : taskInfo) {
                    task.finishAndRemoveTask();
                }
                taskInfo=am.getAppTasks();
                Log.v("TakeCoordinates","Finish " +taskInfo.size());
            }
    }
}