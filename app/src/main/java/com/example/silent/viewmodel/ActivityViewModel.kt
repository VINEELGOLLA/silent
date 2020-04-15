package com.example.silent.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.silent.LocationRepository

class ActivityViewModel(application: Application): AndroidViewModel(application) {

    private var repository: LocationRepository =
        LocationRepository(application)


    fun getactivelocation() = repository.getactivelocation()

    fun setstatusfalse() = repository.setstatusfalse()


}