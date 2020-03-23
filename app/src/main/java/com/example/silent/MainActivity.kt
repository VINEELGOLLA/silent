package com.example.silent

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat



class MainActivity : AppCompatActivity() {

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        //lateinit var database: LocationDatabase

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestuserlocation()

        //database = Room.databaseBuilder(this,LocationDatabase::class.java, "Location").build()


    }

    private fun requestuserlocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }    }
}
