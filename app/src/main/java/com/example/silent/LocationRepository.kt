package com.example.silent

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.silent.db.Location
import com.example.silent.db.LocationDao
import com.example.silent.db.LocationDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LocationRepository(application: Application) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var locationDao: LocationDao?

    init {
        val db = LocationDatabase.getDatabase(application)
        locationDao = db?.locationDao()
    }

    fun getLocations() = locationDao?.getAllLocations()

    fun addlocation(location: Location){
        launch {
            locationDao?.insert(location)
        }
    }

    fun deleteLocation(location: Location) {
        launch {
            locationDao?.delete(location)
        }
    }

    fun updateLocation(location: Location){
        launch {
            locationDao?.update(location)
        }
    }
    
    fun getlocationid(locationid: String) = locationDao?.getLocation(locationid)

    //fun getLatLngRadius() = locationDao?.getLatLngRadius()


    fun getactivelocation() = locationDao?.checkActiveLocation()

    fun updateswitch(id: String,bool:Boolean){
        launch {
            locationDao?.updateswitch(id,bool)
        }
    }

    fun checkid(id: String): LiveData<Int>? = locationDao?.checkid(id)

    fun setstatusfalse() = locationDao?.setstatusfalse()

}