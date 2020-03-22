package com.example.silent.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface LocationDao {

    @Insert
    fun insert(location: Location)

    @Delete
    fun delete(vararg location: Location)

    @Query("Select * from Location ORDER BY status DESC")
    fun getAllLocations(): LiveData<List<Location>>
}