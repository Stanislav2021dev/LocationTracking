package com.example.locationtask6.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


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
        if (!(isSuccess)) {
            LoadData.uploadToRoomDb(result);
        }

        return Result.success();

    }


}
