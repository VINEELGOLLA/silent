package com.example.silent.model

import java.io.Serializable

data class LocationCheck(val id: String,val lat: Double, val lng: Double,val name: String, val notify: Boolean, val radius: Double, val mode: Int): Serializable