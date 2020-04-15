package com.example.silent.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(Location::class)], version = 1)
abstract class LocationDatabase: RoomDatabase() {

    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var instance: LocationDatabase? = null

        fun getDatabase(context: Context): LocationDatabase? {
            if (instance == null) {
                synchronized(LocationDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                                context.applicationContext,
                                LocationDatabase::class.java, "Location"
                            )
                            .build()
                    }
                }
            }
            return instance
        }
    }
}