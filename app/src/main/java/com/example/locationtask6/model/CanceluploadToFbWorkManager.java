package com.example.locationtask6.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.locationtask6.view.App;

public class CanceluploadToFbWorkManager extends Worker {
    public CanceluploadToFbWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        WorkManager.getInstance(App.getContext()).cancelAllWorkByTag("fb");
        return Result.success();

    }
}
