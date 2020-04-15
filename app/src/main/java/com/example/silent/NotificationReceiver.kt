package com.example.silent

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.silent.db.LocationDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class NotificationReceiver: BroadcastReceiver(),CoroutineScope {
    override fun onReceive(context: Context?, intent: Intent?) {
        val repository: LocationDatabase = LocationDatabase.getDatabase(context!!.applicationContext)!!

        val notificationId: Int = intent!!.getIntExtra("notificationId",0)

        val id: String? = intent.getStringExtra("locationid")

        launch {
            if (id != null) {
                repository.locationDao().updateswitch(id,false)
            }
        }


        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}