package com.example.silent.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PlusCode

@Entity(tableName = "Location")
data class Location (@PrimaryKey val id: String, val name: String, val address: String, val radius: Double, val mode: Int, val notify: Boolean, val lat: Double, val lng: Double, val plusCode: String, val status: Boolean)