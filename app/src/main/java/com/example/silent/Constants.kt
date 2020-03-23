package com.example.silent

import com.google.android.libraries.places.api.model.Place

object Constants {

    const val mile50 = 50.0
    const val mile100 = 100.0
    const val mile150 = 150.0
    const val mile200 = 200.0

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

}