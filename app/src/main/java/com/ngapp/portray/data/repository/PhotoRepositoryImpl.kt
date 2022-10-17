package com.ngapp.portray.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.transition.Transition
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavDeepLinkBuilder
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import androidx.work.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.ngapp.portray.BuildConfig
import com.ngapp.portray.R
import com.ngapp.portray.data.Api
import com.ngapp.portray.data.db.PortrayDatabase
import com.ngapp.portray.data.db.models._dao.PhotoDao
import com.ngapp.portray.data.db.models.photo.Photo
import com.ngapp.portray.data.di.Repository
import com.ngapp.portray.ui.MainActivity
import com.ngapp.portray.ui.home.datasource.PhotoRemoteMediator
import com.ngapp.portray.ui.home.datasource.SearchPhotoPagingSource
import com.ngapp.portray.ui.home.photo_detail.PhotoDetailFragment
import com.ngapp.portray.ui.home.photo_detail.worker.DownloadWorker
import com.ngapp.portray.utils.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import java.io.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: Api,
    private val photoDao: PhotoDao,
    private val portrayDatabase: PortrayDatabase,
    private val retrofit: Retrofit
) : Repository {

    suspend fun getElementList(): List<Photo> {
        return api.getPhotoList()
    }


    suspend fun getElementByIdFromDtb(id: String): Photo? {
        return portrayDatabase.photoDao().getPhotoById(id)
    }

    suspend fun changeLike(
        photoId: String,
        likedByUser: Boolean,
        likes: String
    ): FetchResult<Photo> {
        if (likedByUser) {
            val result = doLikeUnlikeOnApi(api.doUnlikePhoto(photoId))
            if (result.status == FetchResult.Status.SUCCESS) {
                photoDao.updateUnlikePhoto(photoId, (likes.toInt() - 1).toString())
                Timber.e("Unliked on Server")
            }
            return result
        } else {
            val result = doLikeUnlikeOnApi(api.doLikePhoto(photoId))
            if (result.status == FetchResult.Status.SUCCESS) {
                photoDao.updateLikePhoto(photoId, (likes.toInt() + 1).toString())
                Timber.e("Liked on Server")
            }
            return result
        }
    }

    suspend fun triggerDownload(photoId: String, ixId: String) {
        api.triggerDownload(photoId, ixId)
    }

    suspend fun downloadPhoto(photoId: String, url: String, fileName: String) {
        val workData = workDataOf(
            DownloadWorker.DOWNLOAD_PHOTO_ID to photoId,
            DownloadWorker.DOWNLOAD_URL_KEY to url,
            DownloadWorker.DOWNLOAD_FILE_NAME to fileName,
        )

        val workConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .setRequiresBatteryNotLow(false)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workData)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
            .setConstraints(workConstraints)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(DOWNLOAD_WORK_ID, ExistingWorkPolicy.KEEP, workRequest)
    }

    suspend fun cancelWorker() {
        cancelProgressNotification()
        WorkManager.getInstance(context)
            .cancelUniqueWork(DOWNLOAD_WORK_ID)
    }

    suspend fun saveImage(photoId: String, fileName: String, url: String) {
        val notificationId = (0..1000000).random()
        showProgressNotification(fileName, notificationId)

        withContext(Dispatchers.IO) {
            val imageUri = saveImageDetails(fileName)
            downloadImage(url, imageUri)
            makeImageVisible(photoId, url, fileName, imageUri, notificationId)
        }
    }

    private fun saveImageDetails(fileName: String): Uri {
        val volume = if (haveQ()) {
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        } else {
            MediaStore.VOLUME_EXTERNAL
        }

        val imageCollectionUri = MediaStore.Images.Media.getContentUri(volume)
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (haveQ()) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        return context.contentResolver.insert(imageCollectionUri, imageDetails)!!
    }

    private fun makeImageVisible(
        photoId: String,
        url: String,
        fileName: String,
        imageUri: Uri,
        notificationId: Int
    ) {
        if (haveQ().not()) return

        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.IS_PENDING, 0)
        }
        context.contentResolver.update(imageUri, imageDetails, null, null)
        finishProgressNotification(photoId, url, fileName, notificationId)
    }

    private suspend fun downloadImage(url: String, uri: Uri) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            api
                .downloadPhoto(url)
                .byteStream()
                .use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
        }
    }


    @OptIn(ExperimentalPagingApi::class)
    fun getLatestPhotos(itemsPerPage: Int): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = itemsPerPage),
            remoteMediator = PhotoRemoteMediator(api, portrayDatabase),
            pagingSourceFactory = { portrayDatabase.photoDao().getAllPhotos() }
        ).flow
    }

    fun getSearchPhotos(query: String, itemsPerPage: Int): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = itemsPerPage)
        ) {
            SearchPhotoPagingSource(api, query)
        }.flow
    }

    suspend fun getPhotoById(photoId: String): Flow<FetchResult<Photo>> {
        return flow {
            val result = getPhotoByIdFromApi(photoId)
            if (result.status == FetchResult.Status.SUCCESS) {
                result.data?.let { it ->
//                    photoDao.deleteAll(it)
                    photoDao.updatePhoto(it)
                }
            }
            emit(result)

            emit(getPhotoByIdFromDatabase(photoId))
//            emit(FetchResult.loading())


        }.flowOn(Dispatchers.IO)
    }

    private suspend fun getPhotoByIdFromApi(photoId: String): FetchResult<Photo> {
        return getResponse(
            request = { api.getPhotoById(photoId) },
            defaultErrorMessage = "Error fetching Photo details",
            retrofit,
            context
        )
    }

    private suspend fun doLikeUnlikeOnApi(query: Response<Photo>): FetchResult<Photo> {
        return getResponse(
            request = { query },
            defaultErrorMessage = "Error fetching Photo details",
            retrofit,
            context
        )
    }

    private suspend fun getPhotoByIdFromDatabase(photoId: String): FetchResult<Photo> {
        return FetchResult.success(portrayDatabase.photoDao().getPhotoById(photoId))
    }

    private fun showProgressNotification(fileName: String, notificationId: Int) {
        val notificationBuilder = NotificationCompat.Builder(
            context,
            NotificationChannels.DOWNLOAD_CHANNEL_ID
        )
            .setContentTitle(fileName)
            .setContentText("Downloading")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_download)
            .setGroup(GROUP_ID)

        val notification = notificationBuilder
            .setProgress(99999, 1, true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(notificationId, notification)
    }

    private fun finishProgressNotification(
        photoId: String,
        url: String,
        fileName: String,
        notificationId: Int
    ) {
        val bundle = Bundle()
        bundle.apply {
            putString(PHOTO_ID_KEY, photoId)
            putString(PHOTO_URL_KEY, url)
        }

        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.nav_photo_detail)
            .setArguments(bundle)
            .createPendingIntent()

        val notificationBuilder = NotificationCompat.Builder(
            context,
            NotificationChannels.DOWNLOAD_CHANNEL_ID
        )
            .setContentTitle(fileName)
            .setContentText("Download progress")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_download_done)

        val finalNotification = notificationBuilder
            .setContentText("Download complete")
            .setProgress(0, 0, false)
            .setContentIntent(pendingIntent)
            .build()


        NotificationManagerCompat.from(context)
            .notify(notificationId, finalNotification)

//        val summaryNotification =
//            NotificationCompat.Builder(context, NotificationChannels.DOWNLOAD_CHANNEL_ID)
//                .setContentTitle("Summary")
//                .setContentText("Message text from time ${System.currentTimeMillis()}")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setSmallIcon(R.drawable.ic_download)
//                .setGroup(GROUP_ID)
//                .setGroupSummary(true)
//                .build()
//
//        NotificationManagerCompat.from(context).apply {
//            notify(notificationId, finalNotification)
//            notify(SUMMARY_NOTIFICATION_ID, summaryNotification)
//        }
    }

    private fun cancelProgressNotification() {
        val notificationBuilder = NotificationCompat.Builder(
            context,
            NotificationChannels.DOWNLOAD_CHANNEL_ID
        )
            .setContentTitle("Photo downloading")
            .setContentText("Download cancelled")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_download)

        val finalNotification = notificationBuilder
            .setContentText("Download cancelled")
            .setProgress(0, 0, false)
            .build()


        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID, finalNotification)
    }

    companion object {
        private const val DOWNLOAD_WORK_ID = "download_work"
        private var NOTIFICATION_ID = 13
        private const val GROUP_ID = "download_work_group"
        private const val SUMMARY_NOTIFICATION_ID = 333

        private const val PHOTO_ID_KEY = "PHOTO_ID_KEY"
        private const val PHOTO_URL_KEY = "PHOTO_URL_KEY"
    }
}
