package com.example.locationtask6.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.CountDownLatch;


public class UploadToFbWorkManager extends Worker {


    public UploadToFbWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String dateTime = getInputData().getString("DateTime");
        String coordinates = getInputData().getString("Coordinates");

        ResultClass result = new ResultClass(dateTime, LoadData.toLatLng(coordinates));
        boolean isSuccess = LoadData.uploadToFireBase(result);
        //Log.v("TakeCoordinates", "Upload to Fb IS SUCCESS: "+ isSuccess);
        if (!(isSuccess)) {
            LoadData.uploadToRoomDb(result);
        }
        return Result.success();
    }


}
