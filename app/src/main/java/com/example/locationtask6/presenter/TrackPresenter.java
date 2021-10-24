package com.example.locationtask6.presenter;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.locationtask6.di.InjectModelInterface;
import com.example.locationtask6.model.CanceluploadToFbWorkManager;
import com.example.locationtask6.model.GetCoordinates;
import com.example.locationtask6.model.InitWorkManager;
import com.example.locationtask6.model.LoadData;
import com.example.locationtask6.model.ResultClass;
import com.example.locationtask6.model.UploadFromDbToFbWorkManager;
import com.example.locationtask6.model.UploadToFbWorkManager;
import com.example.locationtask6.model.UploadToDbWorkManager;
import com.example.locationtask6.view.App;
import com.example.locationtask6.view.TrackInterface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private Subject<ResultClass> locationPoint;
    private InitWorkManager initWorkManager;
    private final int FOREGROUND_GATHERING_COORDINATES_TIME_INTERVAL = 10*1000;


    public void startLocationUpdates() {
        getCoordinates.startLocationUpdates();
        initWorkManager = new InitWorkManager();
    }

    public void start() {
        Log.v("Order", "Start()");
        InjectModelInterface mInterface = EntryPoints.get(this, InjectModelInterface.class);
        getCoordinates = mInterface.getCoordinates();
        loadData = mInterface.getLoadData();

        locationPoint= getCoordinates.getLocationPoint();
        locationPoint.subscribe(resultClassObserver());

        getCoordinates.buildLocationRequest(FOREGROUND_GATHERING_COORDINATES_TIME_INTERVAL);
        getCoordinates.buildLocationSettingsRequest();
        getCoordinates.buildLocationCallBack();
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
                initWorkManager.initWork(resultClass);
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

    @Override
    public void onDestroy() {
        Log.v("Order","OnDestroy");
        super.onDestroy();

    }
    public LoadData getLoadData(){
        return loadData;
    }
    public GetCoordinates getGetCoordinates(){
        return getCoordinates;
    }
}