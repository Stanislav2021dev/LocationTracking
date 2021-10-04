package com.example.locationtask6.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.locationtask6.view.LogInActivity;


public class UploadToDbWorkManager extends Worker {


    private final Context context = LogInActivity.getContext();

    public UploadToDbWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String dateTime = getInputData().getString("DateTime");
        String coordinates = getInputData().getString("Coordinates");

        ResultClass result = new ResultClass(dateTime, LoadData.toLatLng(coordinates));

        if (!(isOnline())) {
            LoadData.uploadToRoomDb(result);
            Log.v("Location", "WorkStarted  " + isOnline() + " " + coordinates );
        }

        return Result.success();
    }

    public boolean isOnline() {
        ConnectivityManager
                connectivityManager =
                context.getSystemService(ConnectivityManager.class);
        Network currentNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(currentNetwork);
        return currentNetwork != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

    }
}



