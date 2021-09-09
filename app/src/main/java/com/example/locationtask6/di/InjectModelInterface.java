package com.example.locationtask6.di;

import com.example.locationtask6.model.GetCoordinates;
import com.example.locationtask6.model.LoadData;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@EntryPoint
@InstallIn(SingletonComponent.class)
public interface InjectModelInterface {

        default GetCoordinates getCoordinates(){
            return new GetCoordinates();
        }

        default LoadData getLoadData(){
            return new LoadData();
        }

}
