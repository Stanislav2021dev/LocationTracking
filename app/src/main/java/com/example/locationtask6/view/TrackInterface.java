package com.example.locationtask6.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndSingleStrategy;
import moxy.viewstate.strategy.StateStrategyType;

public interface TrackInterface extends MvpView {

    @StateStrategyType(AddToEndSingleStrategy.class)
     void   addPoint(LatLng currentLatLng);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showSnackBar(String mainText, String action, View.OnClickListener listener);




    }


