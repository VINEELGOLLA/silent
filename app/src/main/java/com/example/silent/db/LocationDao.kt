package com.example.silent.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.silent.model.LocationCheck


@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: Location)

    @Delete
    suspend fun delete(vararg location: Location)

    @Query("Select * from Location ORDER BY status DESC")
    fun getAllLocations(): LiveData<List<Location>>

    @Query("Select * from Location where id = :locationid")
    fun getLocation(locationid: String): LiveData<Location>

    @Update
    suspend fun update(location: Location)

    @Query( "SELECT CASE WHEN EXISTS(SELECT * From Location where id = :id) THEN '1' ELSE '0' END")
    suspend fun getcheckid(id: String): Int

    @Query("SELECT id, lat, lng, name, notify, radius, mode From Location where status = 1")
    fun getLatLngRadius(): LiveData<List<LocationCheck>>

    @Query("SELECT COUNT(*) From Location where status = 1")
    fun checkActiveLocation(): LiveData<Int>

    @Query("SELECT id, lat, lng, name, notify, radius, mode From Location where status = 1")
    fun getLatLngRadius1(): List<LocationCheck>

    @Query("UPDATE Location set status = :bool where id = :id")
    suspend fun updateswitch(id: String,bool: Boolean)

    @Query("SELECT COUNT(*) From Location where id = :id")
    fun checkid(id: String): LiveData<Int>

    @Query("UPDATE Location set status = 0")
    fun setstatusfalse()
}