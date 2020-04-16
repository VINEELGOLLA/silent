package com.example.silent.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.silent.db.LocationDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class itemWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var repository: LocationDatabase = LocationDatabase.getDatabase(context)!!


    override fun doWork(): Result {

        System.out.println("itemworker deleted")
        val id = inputData.getString("id")

        if (id != null) repository.locationDao().itemdelete(id)

        return Result.success()
    }
}