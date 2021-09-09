package com.example.locationtask6.presenter;

import android.util.Log;

import com.example.locationtask6.di.InjectModelInterface;
import com.example.locationtask6.model.GetCoordinates;
import com.example.locationtask6.model.LoadData;
import com.example.locationtask6.model.ResultClass;
import com.example.locationtask6.view.TrackInterface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.EntryPoints;
import dagger.hilt.internal.GeneratedComponent;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import moxy.MvpPresenter;

public class TrackPresenter extends MvpPresenter<TrackInterface> implements InjectModelInterface, GeneratedComponent {

    private Subject<ResultClass> locationPoint;
    private GetCoordinates getCoordinates;
    private LoadData loadData;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void start() {

        InjectModelInterface mInterface = EntryPoints.get(this, InjectModelInterface.class);
        getCoordinates = mInterface.getCoordinates();
        loadData = mInterface.getLoadData();

        Log.v("Loc", "Start()");
        locationPoint = PublishSubject.create();
        locationPoint
                .subscribe(resultClassObserver());

        getCoordinates.buildLocationRequest();
        getCoordinates.buildLocationSettingsRequest();
        getCoordinates.buildLocationCallBack(locationPoint);
    }


    public Observer<ResultClass> resultClassObserver() {
        return new Observer<ResultClass>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                executorService.execute(() -> loadData.initDb());
            }

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull ResultClass resultClass) {
                executorService.execute(() -> {
                    Log.v("Loc", "CurrentTread ONnext " + Thread.currentThread().getName());
                    loadData.uploadToRoomDb(resultClass);
                    loadData.uploadToFireBase(resultClass);
                });
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
}
