package com.example.silent

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class NotificationReceiverDB: BroadcastReceiver(),CoroutineScope {
    private var am: AudioManager? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        // audio manager
        am = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager?

        // room database
        //val repository: LocationDatabase = LocationDatabase.getDatabase(context!!.applicationContext)!!


        val notificationId: Int = intent!!.getIntExtra("notificationId",0)

        val mode: Int = intent.getIntExtra("ringermode",0)

        //val id: String? = intent.getStringExtra("locationid")


        if (am!!.ringerMode != mode) {
            when (mode) {
                1 -> mode(mode)
                else -> am!!.ringerMode = mode
            }
        }


        //am!!.ringerMode = mode

        val notificationManager: NotificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    // for vibrate mode less than api 28
    private fun mode(mode: Int){
        if(mode == 1){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                am!!.ringerMode = mode
            }else {
                am!!.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
            }
        }
        else{
            am!!.ringerMode = mode
        }

    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}