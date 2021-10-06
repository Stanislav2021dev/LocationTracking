package com.example.locationtask6.model;

import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.WorkManager;

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
import java.util.List;

public class LoadData {
    private static CoordinatesDataBase coordinatesDataBase;
    private static final HashMap<String, LatLng> coordinates=new HashMap<>();
    private static FirebaseFirestore db;
    private static boolean uploadSuccess;


    public static void initDb(){
        Log.v("Location","InitDb");
        coordinatesDataBase= Room.databaseBuilder(LogInActivity.getContext(),CoordinatesDataBase.class,
                "CoordinatesData").build();
        coordinatesDataBase.getCoordinatesDAO().deleteAllCoordinates();
    }


    public static void uploadToRoomDb(ResultClass resultClass){
        Log.v("Location", "UploadToDb");
        coordinatesDataBase.getCoordinatesDAO().addCoordinates(new CoordinatesModel(0,resultClass.getCurrentDateTime(),
                resultClass.getCurrentLocation().toString()));
    }

    public static boolean uploadToFireBase(ResultClass resultClass){

        coordinates.put(resultClass.getCurrentDateTime(),resultClass.getCurrentLocation());
        db = FirebaseFirestore.getInstance();
        db.collection("coordinates")
                .add(coordinates)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.v("Location","UploadToFb");
                        uploadSuccess=true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("Location","Error ->" +e);
                        uploadSuccess=false;
                    }
                });
        return uploadSuccess;
    }

    public static LatLng  toLatLng(String coordinates){
        String[] latlong = coordinates.split(",");
        double latitude = Double.parseDouble(latlong[0].replace("lat/lng: (",""));
        double longitude = Double.parseDouble(latlong[1].replace(")",""));
        return new LatLng(latitude, longitude);
    }
    public static CoordinatesDataBase getCoordinatesDataBase(){
        return coordinatesDataBase;
    }

}


