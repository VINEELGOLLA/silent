package com.example.silent.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import androidx.work.Operation
import com.example.silent.Location_detail


@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: Location)

    @Delete
    suspend fun delete(vararg location: Location)

    //@Query("Select * from Location ORDER BY status DESC")
    @Query("Select * from Location ORDER BY status DESC")
    fun getAllLocations(): LiveData<List<Location>>

    @Query("Select * from Location where id = :locationid")
    fun getLocation(locationid: String): LiveData<Location>

    @Update
    suspend fun update(location: Location)

    @Query( "SELECT CASE WHEN EXISTS(SELECT * From Location where id = :id) THEN '1' ELSE '0' END")
    suspend fun getcheckid(id: String): Int
}