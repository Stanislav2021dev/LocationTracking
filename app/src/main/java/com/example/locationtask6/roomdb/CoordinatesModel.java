package com.example.locationtask6.roomdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "coordinates_table")
public class CoordinatesModel {


    @ColumnInfo(name = "coordinates_id")
    @PrimaryKey(autoGenerate = true)
    public long id;


    @ColumnInfo(name = "coordinates_date_time")
    public String dateTime;

    @ColumnInfo(name = "coordinates_coordinates")
    public String coordinates;


    public CoordinatesModel(long id, String dateTime, String coordinates) {
        this.id = id;
        this.dateTime = dateTime;
        this.coordinates = coordinates;
    }


    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }


}
