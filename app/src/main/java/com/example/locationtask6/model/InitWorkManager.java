package com.example.locationtask6.model;

import android.annotation.SuppressLint;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.locationtask6.view.App;

import java.util.concurrent.TimeUnit;

public class InitWorkManager {

    public void initWork(ResultClass resultClass){

        @SuppressLint("RestrictedApi")
        Data inputData=new Data.Builder()
                .putString("DateTime",resultClass.getCurrentDateTime())
                .putString("Coordinates",resultClass.getCurrentLocation().toString())
                .build();

        Constraints uploadToDbConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();

        Constraints uploadToFbConstraints=new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest uploadToFbRequest=new OneTimeWorkRequest.Builder(UploadToFbWorkManager.class)
                .setInputData(inputData)
                .addTag("fb")
                .setConstraints(uploadToFbConstraints).build();

        OneTimeWorkRequest uploadToDbRequest = new OneTimeWorkRequest.Builder(UploadToDbWorkManager.class)
                .setInputData(inputData)

                .setConstraints(uploadToDbConstraints).build();

        OneTimeWorkRequest uploadFromDbToFbRequest = new OneTimeWorkRequest.Builder(UploadFromDbToFbWorkManager.class)
                .setConstraints(uploadToFbConstraints)
                .build();

        OneTimeWorkRequest canceluploadToFb = new OneTimeWorkRequest.Builder(CanceluploadToFbWorkManager.class)
                .setInitialDelay(1000, TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(App.getContext())
                .beginWith(uploadToDbRequest)
                .then(uploadToFbRequest)
                .enqueue();

        WorkManager.getInstance(App.getContext())
                .beginWith(canceluploadToFb)
                .enqueue();

        WorkManager.getInstance(App.getContext())
                .enqueueUniqueWork("work", ExistingWorkPolicy.REPLACE,uploadFromDbToFbRequest);

    }


}
