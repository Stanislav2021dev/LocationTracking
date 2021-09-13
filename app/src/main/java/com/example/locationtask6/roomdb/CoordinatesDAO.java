package com.example.locationtask6.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CoordinatesDAO {

        @Insert
        void addCoordinates(CoordinatesModel coordinatesModel);

        @Query("DELETE FROM coordinates_table")
        void deleteAllCoordinates();

        @Query("DELETE FROM coordinates_table WHERE coordinates_id=:id ")
        void delete(long id);

        @Query("SELECT * FROM coordinates_table")
        List<CoordinatesModel> getAllCoordinates();

}
