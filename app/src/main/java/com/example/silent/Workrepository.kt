package com.example.silent

import androidx.work.*
import com.example.silent.workers.LocationWorker
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.TimeUnit

class Workrepository {

     fun setupRecurringWork(workmanager: WorkManager): String {

        if(!isWorkScheduled(workmanager)) {
            val repeatingRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES).build()
            workmanager.enqueueUniquePeriodicWork(Constants.WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, repeatingRequest)
            //System.out.println("work manager started")
        }else{
            return "already work manager running"
        }
         return "work manager started"

     }

    private fun isWorkScheduled(workmanager: WorkManager): Boolean {
        var running = false
        //val instance = WorkManager.getInstance(application)
        val statuses: ListenableFuture<MutableList<WorkInfo>> = workmanager.getWorkInfosForUniqueWork(Constants.WORK_NAME)

        if(statuses.get().size > 0){
            val state = statuses.get()[0].state
            running = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
        }
        return running
    }

    fun stopworkmanager(workmanager: WorkManager) : String{
        return if(isWorkScheduled(workmanager)){
            workmanager.cancelUniqueWork(Constants.WORK_NAME)
            //Toast.makeText(C,"jobs cancelled", Toast.LENGTH_LONG).show()
            //System.out.println("jobs cancelled")
            "jobs cancelled"
        } else{
            //println("work manager not started")
            "work manager not started"
        }
    }
}