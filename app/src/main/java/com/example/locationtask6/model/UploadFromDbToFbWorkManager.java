package com.example.locationtask6.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.locationtask6.roomdb.CoordinatesModel;

import java.util.List;

public class UploadFromDbToFbWorkManager extends Worker {

    private List<CoordinatesModel> coordinatesModelList;

    public UploadFromDbToFbWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        coordinatesModelList =
                LoadData.getCoordinatesDataBase().getCoordinatesDAO().getAllCoordinates();
        Log.v("Data", "Size" + coordinatesModelList.size());
        if (coordinatesModelList.size() != 0) {
            uploadFromDbToFb();
        }

        return Result.success();
    }

    public void uploadFromDbToFb() {

        for (CoordinatesModel coord : coordinatesModelList) {

            Log.v("Data", "Time " + coord.getDateTime() + " coordinates " + coord.getCoordinates()
                    + "Size " + LoadData.getCoordinatesDataBase().getCoordinatesDAO().getAllCoordinates().size());

            LoadData.uploadToFireBase(new ResultClass(coord.getDateTime(), LoadData.toLatLng(coord.getCoordinates())));
            LoadData.getCoordinatesDataBase().getCoordinatesDAO().delete(coord.getId());
        }
    }
}



