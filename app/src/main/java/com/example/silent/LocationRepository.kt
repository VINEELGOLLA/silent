package com.example.silent

import android.app.Application
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

    fun checkId(id:String){
        launch {
            locationDao?.getcheckid(id)
        }
    }
    
    fun getlocationid(locationid: String) = locationDao?.getLocation(locationid)
}