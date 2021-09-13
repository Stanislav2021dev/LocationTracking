package com.example.locationtask6.presenter;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;

import com.example.locationtask6.di.InjectModelInterface;
import com.example.locationtask6.model.GetCoordinates;
import com.example.locationtask6.model.LoadData;
import com.example.locationtask6.model.ResultClass;
import com.example.locationtask6.model.UploadToFbWorkManager;
import com.example.locationtask6.model.UploadToDbWorkManager;
import com.example.locationtask6.view.LogInActivity;
import com.example.locationtask6.view.TrackInterface;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.EntryPoints;
import dagger.hilt.internal.GeneratedComponent;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.Subject;
import moxy.MvpPresenter;

public class TrackPresenter extends MvpPresenter<TrackInterface> implements InjectModelInterface, GeneratedComponent {


    private GetCoordinates getCoordinates;
    private LoadData loadData;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ComponentName componentName;
    private Subject<ResultClass> locationPoint;

    public void start() {

        InjectModelInterface mInterface = EntryPoints.get(this, InjectModelInterface.class);
        getCoordinates = mInterface.getCoordinates();
        loadData = mInterface.getLoadData();

        Log.v("Loc", "Start()");
        locationPoint= getCoordinates.getLocationPoint();
        locationPoint.subscribe(resultClassObserver());

        getCoordinates.buildLocationRequest();
        getCoordinates.buildLocationSettingsRequest();
        getCoordinates.buildLocationCallBack();


    }



    @RequiresApi(api = Build.VERSION_CODES.R)
    public void startWork(ResultClass resultClass){

        @SuppressLint("RestrictedApi")
        Data inputData=new Data.Builder()
                .putString("DateTime",resultClass.getCurrentDateTime())
                .putString("Coordinates",resultClass.getCurrentLocation().toString())
                .build();

        Constraints  uploadToDbConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();

       Constraints uploadToFbConstraints=new Constraints.Builder()
               .setRequiredNetworkType(NetworkType.CONNECTED)
               .build();

        OneTimeWorkRequest uploadToFbRequest=new OneTimeWorkRequest.Builder(UploadToFbWorkManager.class)
                .setInputData(inputData)
                .setConstraints(uploadToFbConstraints).build();

        OneTimeWorkRequest uploadToDbRequest = new OneTimeWorkRequest.Builder(UploadToDbWorkManager.class)
                .setInputData(inputData)
                .setConstraints(uploadToDbConstraints).build();


        WorkManager.getInstance(LogInActivity.getContext())
                .beginWith(uploadToDbRequest)
                .then(uploadToFbRequest)
                .enqueue();
    }



    public Observer<ResultClass> resultClassObserver() {
        return new Observer<ResultClass>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                executorService.execute(() -> LoadData.initDb());

            }

            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResultClass resultClass) {

               startWork(resultClass);

                getViewState().addPoint(resultClass.getCurrentLocation());
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    public void startLocationUpdates() {
        getCoordinates.startLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getCoordinates.stopLocationUpdates();
    }
    public LoadData getLoadData(){
        return loadData;
    }
}