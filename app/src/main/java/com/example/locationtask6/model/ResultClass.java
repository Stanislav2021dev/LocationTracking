package com.example.locationtask6.model;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.rxjava3.subjects.Subject;

public class ResultClass {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HHmmss", Locale.US);

  private String currentDateTime;
  private LatLng currentLocation;


  public  ResultClass(String currentDateTime,LatLng currentLocation){
      this.currentDateTime=currentDateTime;
      this.currentLocation=currentLocation;
  }

  public String getCurrentDateTime(){
     // currentDateTime = DATE_FORMAT.format(new Date());
      return currentDateTime;
  }

  public LatLng getCurrentLocation(){
      return currentLocation;
  }

}
