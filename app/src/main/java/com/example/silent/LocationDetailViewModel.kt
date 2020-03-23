package com.example.silent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.silent.db.Location

class LocationDetailViewModel(application: Application) : AndroidViewModel(application) {
    private var repository:LocationRepository = LocationRepository(application)


    fun deleteLocation(location: Location){
        repository.deleteLocation(location)
    }

    fun updateLocation(location: Location){
        repository.updateLocation(location)
    }

    fun getlocation(locationid: String) = repository.getlocationid(locationid)



}
