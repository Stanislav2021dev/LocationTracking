package com.example.locationtask6.model;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.locationtask6.roomdb.CoordinatesDataBase;
import com.example.locationtask6.roomdb.CoordinatesModel;
import com.example.locationtask6.view.LogInActivity;
import com.example.locationtask6.view.TrackActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class LoadData {
    private CoordinatesDataBase coordinatesDataBase;
    private static final HashMap<String, LatLng> coordinates=new HashMap<>();
    private FirebaseFirestore db;

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    public void initDb(){
        Log.v("Loc","InitDb");
        coordinatesDataBase= Room.databaseBuilder(LogInActivity.getContext(),CoordinatesDataBase.class,
                "CoordinatesData").build();
        coordinatesDataBase.getCoordinatesDAO().deleteAllCoordinates();
    }


    public void uploadToRoomDb(ResultClass resultClass){
        Log.v("Loc", "UploadToDb");
        coordinatesDataBase.getCoordinatesDAO().addCoordinates(new CoordinatesModel(0,resultClass.getCurrentDateTime(),
                resultClass.getCurrentLocation().toString()));
    }

    public void uploadToFireBase(ResultClass resultClass){
        coordinates.put(resultClass.getCurrentDateTime(),resultClass.getCurrentLocation());
        db = FirebaseFirestore.getInstance();
        db.collection("coordinates")
                .add(coordinates)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.v("Loc","CoordinatesModel added to Fb");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("Loc","Error ->" +e);
                    }
                });

    }
}


