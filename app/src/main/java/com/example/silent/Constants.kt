package com.example.silent

import com.google.android.libraries.places.api.model.Place

object Constants {

    //0.1 mile in meters
    const val mile50 = 160.934
    // 0.2 mile in meters
    const val mile100 = 321.869
    // 03 mile in meters
    const val mile150 = 482.803
     //0.5 mile in meters
    const val mile200 = 804.672

    // Type of fields we want from place search
    val fields = listOf(
        Place.Field.ID, Place.Field.NAME,
        Place.Field.LAT_LNG,
        Place.Field.ADDRESS,
        Place.Field.TYPES,
        Place.Field.PLUS_CODE
    )

    const val api_key = "AIzaSyCvHDX7gaYf9HnxoRgEjvWK779WgAMckSA"

    const val peekheight = 270

    const val WORK_NAME = "com.example.silent"

}