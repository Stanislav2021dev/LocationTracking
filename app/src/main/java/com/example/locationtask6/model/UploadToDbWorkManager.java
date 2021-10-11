package com.example.locationtask6.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class UploadToDbWorkManager extends Worker {
    public UploadToDbWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String dateTime = getInputData().getString("DateTime");
        String coordinates = getInputData().getString("Coordinates");

        ResultClass result = new ResultClass(dateTime, LoadData.toLatLng(coordinates));
        LoadData.uploadToRoomDb(result);
        Log.v("Location", "WorkStarted  " + Utils.isOnline() + " " + coordinates);
        return Result.success();
    }
}



