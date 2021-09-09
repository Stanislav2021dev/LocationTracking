package com.example.locationtask6.model;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.reactivex.rxjava3.subjects.Subject;

public class ResultClass {

  private String currentDateTime;
  private LatLng currentLocation;

  public  ResultClass(String currentDateTime,LatLng currentLocation){
      this.currentDateTime=currentDateTime;
      this.currentLocation=currentLocation;
  }

  public String getCurrentDateTime(){
      currentDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
      return currentDateTime;
  }

  public LatLng getCurrentLocation(){
      return currentLocation;
  }

}
