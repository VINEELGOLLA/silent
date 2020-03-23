package com.example.silent

import android.app.Application
import androidx.lifecycle.AndroidViewModel



class UserHomePageViewModel(application: Application) : AndroidViewModel(application) {

    private var repository:LocationRepository = LocationRepository(application)

    fun getLocations() = repository.getLocations()





}
