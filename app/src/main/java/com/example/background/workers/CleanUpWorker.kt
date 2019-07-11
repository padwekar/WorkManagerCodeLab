package com.example.background.workers

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import com.example.background.PARENT_PATH
import java.io.File

class CleanUpWorker(context: Context,workerParameters: WorkerParameters) : Worker(context,workerParameters) {
    override fun doWork(): Result {
        makeStatusNotification("Cleaning up old temporary files", applicationContext)
        sleep()
        return try {
            val outputDirectory = File(PARENT_PATH, OUTPUT_PATH)
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null) {
                    for (entry in entries) {
                        val name = entry.name
                        if (name.isNotEmpty() && name.endsWith(".png")) {
                            val deleted = entry.delete()
                            Log.i(TAG, String.format("Deleted %s - %s", name, deleted))
                        }
                    }
                }
            }
            Result.success()
        } catch (exception: Exception) {
            Log.e(TAG, "Error cleaning up", exception)
            Result.failure()
        }
    }
}