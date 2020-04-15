package com.example.silent.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.example.silent.Permissioncheck
import com.example.silent.R
import com.example.silent.Workrepository
import com.example.silent.viewmodel.ActivityViewModel
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private var permissioncheck: Permissioncheck = Permissioncheck()
    private val workrepository: Workrepository = Workrepository()

    private var checkagain: Int = 0
    // notification channel
    private val channelid = "Channel_ID"
    private val channelName = "Ringer Mode"
    private lateinit var mNotificationManager: NotificationManager

    private val workManager: WorkManager = WorkManager.getInstance(application)

    private var permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
            )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
        )
    }


    private lateinit var viewModel: ActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        createNotificationChannel()

        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        requestuserlocation()

        viewModel = ViewModelProvider(this).get(ActivityViewModel::class.java)


        acticveloc()


    }

    private fun acticveloc(){
        viewModel.getactivelocation()?.observe(this, Observer {
            checkagain = it
            if(it > 0){
                Timer().schedule(1000){
                    //checkper()
                    when(permissioncheck.check(this@MainActivity,mNotificationManager)) {
                        "setupRecurringWork" -> when(workrepository.setupRecurringWork(workManager)){
                            //"already work manager running" -> displaytoast("already work manager running")
                            "already work manager running" -> {
                                //displaytoast("already running")
                            }
                            "work manager started" -> {
                                //displaytoast("work manager started")

                            }
                        }
                        "toast" -> {
                            //System.out.println("true lol")
                            displaytoast("permissions required")
                            workrepository.stopworkmanager(workManager)
                            viewModel.setstatusfalse()
                        }
                    }
                }
            }else{
                //stopworkmanager()
                when(workrepository.stopworkmanager(workManager)){
                    //"jobs cancelled" -> displaytoast("jobs cancelled")
                    //"work manager not started" -> displaytoast("work manager not started")
                }
                //println("no active")
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestuserlocation()
            } else {
                println("location permission granted")
            }
        }else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestuserlocation()
            } else {
                println("location permission granted")
            }
        }
        println("called call")
    }

    private fun requestuserlocation() {
        ActivityCompat.requestPermissions(this,permissions,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelid,channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //val channl = manager.getNotificationChannel(channelid)
            manager.createNotificationChannel(channel)

        }
    }

    override fun onPause() {
        viewModel.getactivelocation()?.removeObservers(this)
        super.onPause()
    }

    override fun onResume() {
        acticveloc()
        if (checkagain > 0 ) {
            //checkper()
            when(permissioncheck.check(this@MainActivity,mNotificationManager)) {
                "setupRecurringWork" -> when(workrepository.setupRecurringWork(workManager)){
                    //"already work manager running" -> displaytoast("already work manager running")
                }
                "toast" -> displaytoast("permissions required")
            }
        }
        super.onResume()
    }

    private fun displaytoast(text: String) {
        runOnUiThread{
            Toast.makeText(this,text,Toast.LENGTH_LONG).show()
        }
    }
}
