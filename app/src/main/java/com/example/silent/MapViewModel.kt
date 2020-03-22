package com.example.silent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)

    fun getLocationData() = locationData

    fun add(){
        locationData.add()
    }

    fun remove(){
        locationData.remove()
    }


}
