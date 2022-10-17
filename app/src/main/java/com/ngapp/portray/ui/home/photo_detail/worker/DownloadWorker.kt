package com.ngapp.portray.ui.home.photo_detail.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ngapp.portray.data.repository.PhotoRepositoryImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val photoRepository: PhotoRepositoryImpl
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val photoId = inputData.getString(DOWNLOAD_PHOTO_ID)
        val urlToDownload = inputData.getString(DOWNLOAD_URL_KEY)
        val fileName = inputData.getString(DOWNLOAD_FILE_NAME)

        urlToDownload?.let { url ->
            Timber.d("work started")
            return try {
                photoRepository.saveImage(photoId!!, fileName!!, url)
                Result.success()
            } catch (e: Exception) {
                Timber.d("Worker exception = $e")
                Result.failure()
            }
        }
        return Result.failure()
    }


    companion object {
        const val DOWNLOAD_PHOTO_ID = "download_photo_id"
        const val DOWNLOAD_URL_KEY = "download_url_key"
        const val DOWNLOAD_FILE_NAME = "download_file_name"
    }
}