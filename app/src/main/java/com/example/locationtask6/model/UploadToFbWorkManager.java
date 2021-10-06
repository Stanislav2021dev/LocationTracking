package com.example.locationtask6.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.locationtask6.roomdb.CoordinatesDataBase;
import com.example.locationtask6.roomdb.CoordinatesModel;

import java.util.List;


public class UploadToFbWorkManager extends Worker {
    public UploadToFbWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {

        String dateTime=getInputData().getString("DateTime");
        String coordinates = getInputData().getString("Coordinates");

        boolean isSuccess = LoadData.uploadToFireBase(new ResultClass(dateTime,LoadData.toLatLng(coordinates)));
        if (!(isSuccess)) {
            LoadData.uploadToRoomDb(new ResultClass(dateTime,LoadData.toLatLng(coordinates)));
        }

        return Result.success();

    }


}
