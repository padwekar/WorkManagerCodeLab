package com.example.background.workers

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class BlurWorker(context: Context,workerParameters: WorkerParameters) : Worker(context,workerParameters) {

    var parameters = workerParameters


    override fun doWork(): Result {
        val appContext = applicationContext

        var outputUri : Uri = Uri.parse("")

        makeStatusNotification("Blurring image", appContext)

        try {
          val bitmap =  MediaStore.Images.Media.getBitmap(appContext.contentResolver, Uri.parse(parameters.inputData.getString("input_uri")))

            val output = blurBitmap(bitmap,applicationContext)

            outputUri = writeBitmapToFile(appContext, output)

            makeStatusNotification("Output is $outputUri", appContext)

        } catch (throwable : Throwable){

            Log.e(TAG, "Error applying blur", throwable)
            Result.failure()

        }

        val outputData = workDataOf("input_uri" to outputUri.toString())

        return Result.success(outputData)
    }

}