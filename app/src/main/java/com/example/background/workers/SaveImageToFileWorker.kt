package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap



class SaveImageToFileWorker(context: Context,workerParameters: WorkerParameters) : Worker(context,workerParameters) {

    var appContext = context
    var parameters = workerParameters


    private val TAG by lazy { SaveImageToFileWorker::class.java.simpleName }
    private val Title = "Blurred Image"
    private val dateFormatter = SimpleDateFormat(
            "yyyy.MM.dd 'at' HH:mm:ss z",
            Locale.getDefault()
    )

    override fun doWork(): Result {
        makeStatusNotification("Saving image", applicationContext)
        sleep()

        val resolver = appContext.contentResolver
        try {
            val resourceUri = Uri.parse(inputData.getString("input_uri"))
            val bitmap = BitmapFactory.decodeStream(resolver.openInputStream(resourceUri))
            val imageURL = MediaStore.Images.Media.insertImage(resolver,bitmap,Title,dateFormatter.format(Calendar.getInstance().timeInMillis))

            if(!imageURL.isNullOrEmpty()){
                val output = workDataOf("output_uri" to imageURL)

                makeStatusNotification("Done Successfully", applicationContext)

                return Result.success(output)
            }
        }catch (e : Exception){
                return Result.failure()
        }

        return Result.failure()
    }
}