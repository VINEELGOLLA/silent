package com.example.silent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.silent.db.Location

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)

    private var repository:LocationRepository = LocationRepository(application)


    fun getLocationData() = locationData

    fun add(){
        locationData.add()
    }

    fun remove(){
        locationData.remove()
    }

   fun checkID(id: String){
        repository.checkId(id)
    }


    fun addlocation(location: Location){
        repository.addlocation(location)
    }




}
