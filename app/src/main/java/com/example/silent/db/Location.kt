package com.example.silent.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Location")
data class Location (@PrimaryKey val id: String, val name: String, val address: String, val radius: Double, val mode: Int, val notify: Boolean, val lat: Double, val lng: Double, val plusCode: String, val status: Boolean)