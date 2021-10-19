package com.example.locationtask6.model;

import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.WorkManager;

import com.example.locationtask6.roomdb.CoordinatesDataBase;
import com.example.locationtask6.roomdb.CoordinatesModel;
import com.example.locationtask6.view.App;
import com.example.locationtask6.view.LogInActivity;
import com.example.locationtask6.view.TrackActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
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


    public static synchronized void initDb(){
        Log.v("Data","InitDb");
        if (coordinatesDataBase != null) {
            return;
        }

        coordinatesDataBase= Room.databaseBuilder(App.getInstance(),CoordinatesDataBase.class,
                "CoordinatesData").build();
        coordinatesDataBase.getCoordinatesDAO().deleteAllCoordinates();
    }


    public static void uploadToRoomDb(ResultClass resultClass){
        if (!Utils.isOnline()) {
            Log.v("TakeCoordinates", "Upload coordinates to local DataBase");
            coordinatesDataBase.getCoordinatesDAO().addCoordinates(new CoordinatesModel(0, resultClass.getCurrentDateTime(),
                    resultClass.getCurrentLocation().toString()));
        }
    }

    public static boolean uploadToFireBase(ResultClass resultClass){

        coordinates.put(resultClass.getCurrentDateTime(),resultClass.getCurrentLocation());
        db = FirebaseFirestore.getInstance();
        db.collection("coordinates")
                .add(coordinates)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.v("TakeCoordinates","Upload coordinates to firebase");
                        uploadSuccess=true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("TakeCoordinates","Error ->" +e);
                        uploadSuccess=false;
                    }
                });
        return uploadSuccess;
    }

    public static void uploadFromDbToFb() {

        List<CoordinatesModel> coordinatesModelList;
        coordinatesModelList = LoadData.getCoordinatesDataBase().getCoordinatesDAO().getAllCoordinates();

        if (coordinatesModelList.size() != 0) {
            Log.v("TakeCoordinates","Upload from DB to Fb");
            for (CoordinatesModel coord : coordinatesModelList) {
       //         Log.v("TakeCoordinates", "Time " + coord.getDateTime() + " coordinates " + coord.getCoordinates()
       //                 + "Size " + LoadData.getCoordinatesDataBase().getCoordinatesDAO().getAllCoordinates().size());
                LoadData.uploadToFireBase(new ResultClass(coord.getDateTime(), LoadData.toLatLng(coord.getCoordinates())));
                LoadData.getCoordinatesDataBase().getCoordinatesDAO().delete(coord.getId());
            }
        }
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


