package com.example.silent.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.silent.LocationRepository
import com.example.silent.db.Location


class UserHomePageViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: LocationRepository =
        LocationRepository(application)


    fun getLocations() = repository.getLocations()

    fun updateswitch(id: String,bool:Boolean) = repository.updateswitch(id,bool)

    fun deleteLocation(location: Location){
        repository.deleteLocation(location)
    }

    fun addlocation(location: Location){
        repository.addlocation(location)
    }


}
