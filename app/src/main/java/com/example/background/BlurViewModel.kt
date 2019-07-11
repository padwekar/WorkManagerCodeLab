/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.background

import androidx.lifecycle.ViewModel
import android.net.Uri
import androidx.work.*
import com.example.background.workers.BlurWorker
import com.example.background.workers.CleanUpWorker
import com.example.background.workers.SaveImageToFileWorker
import com.example.background.workers.makeStatusNotification


class BlurViewModel : ViewModel() {

    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null
    val workManager = WorkManager.getInstance()

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    fun applyBlur(level : Int = 1){

        val cleanUpWork = OneTimeWorkRequestBuilder<CleanUpWorker>().build()

        var continuation = workManager.beginUniqueWork(
                        IMAGE_MANIPULATION_WORK_NAME,
                        ExistingWorkPolicy.REPLACE, cleanUpWork)


        val blurWorkBuilder = OneTimeWorkRequestBuilder<BlurWorker>()
               .setInputData(Data.Builder().putString("input_uri",imageUri.toString()).build())

        val saveImageToFileWorker = OneTimeWorkRequestBuilder<SaveImageToFileWorker>().build()


        for(i in 0..level){
            if(i==0){
                blurWorkBuilder.setInputData(Data.Builder().putString("input_uri",imageUri.toString()).build())
            }
            continuation = continuation.then(blurWorkBuilder.build())
        }

        continuation = continuation.then(saveImageToFileWorker)
        continuation.enqueue()


    }

    internal fun cancelWork(){
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    /**
     * Setters
     */
    internal fun setImageUri(uri: String?) {
        imageUri = uriOrNull(uri)
    }

    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }
}
