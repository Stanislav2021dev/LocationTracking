package com.example.locationtask6.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UploadFromDbToFbWorkManager extends Worker {


    public UploadFromDbToFbWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        LoadData.uploadFromDbToFb();
        return Result.success();
    }
}



