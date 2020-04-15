package com.example.silent.workers

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.media.AudioManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.silent.*
import com.example.silent.db.LocationDatabase
import com.example.silent.model.LocationCheck
import com.example.silent.view.MainActivity
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.PI
import kotlin.coroutines.CoroutineContext


class LocationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main


    private var notificationManager: NotificationManager? = null

    private var permissioncheck: Permissioncheck = Permissioncheck()




    private var mode1: String = "mode"

    private val channelid = "Channel_ID"
    //private val notificatioID: Int = (0..100000).random()

    private var  notificatioID: Int = 0

    private var am: AudioManager? = null

    private  var lol: List<LocationCheck>? = null

    private var repository: LocationDatabase = LocationDatabase.getDatabase(context)!!

    //private val TAG = "MyWorker"

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val workManager: WorkManager = WorkManager.getInstance(applicationContext)

    private lateinit var location: Location


    override fun doWork(): Result {

        //Log.d(TAG, "doWork: Done")

        //Log.d(TAG, "onStartJob: STARTING JOB..")

        notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        when(permissioncheck.check(applicationContext, notificationManager!!)){
            "toast" ->{
                shownotificationfornoaccess()
                workManager.cancelUniqueWork(Constants.WORK_NAME)
            }else -> {

            lol = repository.locationDao().getLatLngRadius1()

            if (lol!!.isEmpty()){
                //Log.d(TAG, "doWork: hjklkjhgghjkj")
                workManager.cancelUniqueWork(Constants.WORK_NAME)
            }


            fusedLocationClient.lastLocation
                .addOnSuccessListener { location1: Location? ->
                    location1?.also {
                        location = it
                        checkradius()
                    }
                }

        }
        }

     /*   if(notificationManager?.isNotificationPolicyAccessGranted!! == false && ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
            shownotificationfornoaccess()
            workManager.cancelUniqueWork(Constants.WORK_NAME)
        }*/
/*

        lol = repository.locationDao().getLatLngRadius1()

        if (lol!!.isEmpty()){
            //Log.d(TAG, "doWork: hjklkjhgghjkj")
            workManager.cancelUniqueWork(Constants.WORK_NAME)
        }


        fusedLocationClient.lastLocation
            .addOnSuccessListener { location1: Location? ->
                location1?.also {
                         location = it
                        checkradius()
                }
            }
*/


        return Result.success()
    }


    private fun checkradius() {
        am = applicationContext.getSystemService(AUDIO_SERVICE) as AudioManager?
        // converted from miles to meters
        for (vineel in this.lol!!){
            //val dist = milestometers(LatLng(vineel.lat,vineel.lng))

            if (milestometers(LatLng(vineel.lat,vineel.lng)) <=  vineel.radius){
                when(vineel.notify){
                    false -> {
                        //delete once activated
                        delete(vineel.id)

                        if (am!!.ringerMode != vineel.mode) {
                            when (vineel.mode) {
                                1 -> mode(vineel.mode)
                                else -> am!!.ringerMode = vineel.mode
                            }
                            shownotificationwithoutaction(vineel.name, vineel.mode,(vineel.lat * 1000000).toInt())
                        }
                    }

                    true -> {
                        shownotificationwithaction(vineel.name,vineel.mode,vineel.id,(vineel.lat * 1000000).toInt())

                    }
                }
            }
        }
    }



    private fun milestometers(lat: LatLng) : Double{
        return kotlin.math.acos(
            kotlin.math.sin(PI * location.latitude / 180.0) * kotlin.math.sin(PI * lat.latitude / 180.0) + kotlin.math.cos(
                PI * location.latitude / 180.0
            ) * kotlin.math.cos(PI * lat.latitude / 180.0) * kotlin.math.cos(PI * location.longitude / 180.0 - PI * lat.longitude / 180.0)
        ) *3963 * 1609.344
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

    private fun delete(id:String){
        launch {
            repository.locationDao().updateswitch(id,false)
        }
    }

    private fun shownotificationwithoutaction(name: String, mode: Int, lat: Int){
        notificatioID = lat
        when (mode) {
            0 -> mode1 = "silent"
            1 -> mode1 = "vibrate"
            2 -> mode1 = "normal"
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }


        val notification = NotificationCompat.Builder(applicationContext, channelid)
            .setContentTitle(name)
            .setContentText("$mode1  mode  on")
            .setSmallIcon(R.drawable.ic_place_black_24dp)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            //.setColor(Color.WHITE)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        //println("id from worker " + notificatioID)

        notificationManager.notify(notificatioID, notification)
    }

    private fun shownotificationwithaction(name: String, mode: Int, id: String, lat: Int){
        notificatioID = lat
        when (mode) {
            0 -> mode1 = "silent"
            1 -> mode1 = "vibrate"
            2 -> mode1 = "normal"
        }

        val buttonIntent = Intent(applicationContext, NotificationReceiver::class.java)
        buttonIntent.putExtra("notificationId", notificatioID)
        buttonIntent.putExtra("locationid",id)
        val dismissIntent: PendingIntent = PendingIntent.getBroadcast(applicationContext, 0, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val button2Intent = Intent(applicationContext, NotificationReceiverDB::class.java)
        button2Intent.putExtra("notificationId", notificatioID)
        button2Intent.putExtra("locationid",id)
        button2Intent.putExtra("ringermode",mode)
        val activateIntent: PendingIntent = PendingIntent.getBroadcast(applicationContext, 0, button2Intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(applicationContext, channelid)
            .setContentTitle(name)
            .setContentText("$mode1  mode  on")
            .setSmallIcon(R.drawable.ic_place_black_24dp)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
             //.setContentIntent(pendingIntent)
            .addAction(R.drawable.silent_icon, "okay", activateIntent)
            .addAction(R.drawable.silent_icon,"dismiss",dismissIntent)
            .setAutoCancel(true)
            .setColor(Color.WHITE)
                //20 minutes in milliseconds
            .setTimeoutAfter(1200000)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        //println("id from worker " + notificatioID)

        notificationManager.notify(notificatioID, notification)
    }

    private fun shownotificationfornoaccess(){
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelid)
            .setContentTitle("Permission access needed")
            .setContentText("Disabling......")
            .setSmallIcon(R.drawable.ic_place_black_24dp)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            //.setColor(Color.WHITE)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)

        //println("id from worker " + notificatioID)

        notificationManager.notify(notificatioID, notification)
    }
}