package com.example.locationtask6.roomdb;


import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {CoordinatesModel.class},version = 1)
public abstract class CoordinatesDataBase extends RoomDatabase {

    public abstract CoordinatesDAO getCoordinatesDAO();


}
