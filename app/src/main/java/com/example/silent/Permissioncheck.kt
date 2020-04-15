package com.example.silent

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class Permissioncheck {

    fun check(context: Context, notimanager: NotificationManager): String{


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return if (notimanager.isNotificationPolicyAccessGranted && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED){
                "setupRecurringWork"
            } else{
                //println("permissions")
                "toast"
            }
            }
        else{
            return if (notimanager.isNotificationPolicyAccessGranted && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                "setupRecurringWork"
            } else{
                //println("permissions")
                "toast"

            }

            }
        }
    }
