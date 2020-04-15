package com.example.silent.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.silent.LocationLiveData
import com.example.silent.LocationRepository
import com.example.silent.db.Location

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)

    private var repository: LocationRepository =
        LocationRepository(application)


    fun getLocationData() = locationData

    fun add(){
        locationData.add()
    }

    fun remove(){
        locationData.remove()
    }


    fun addlocation(location: Location){
        repository.addlocation(location)
    }

    fun checkid(id: String): LiveData<Int>?{
        return repository.checkid(id)
    }




}
