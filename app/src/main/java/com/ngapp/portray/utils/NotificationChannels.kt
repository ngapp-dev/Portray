package com.ngapp.portray.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat

object NotificationChannels {

    const val DOWNLOAD_CHANNEL_ID = "download"

    fun create(context: Context) {
        if (haveO()) {
            createDownloadChannel(context)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDownloadChannel(context: Context) {
        val name = "Downloads"
        val channelDescription = "Running background processes"
        val priority = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(DOWNLOAD_CHANNEL_ID, name, priority).apply {
            description = channelDescription
            setSound(null, null)
        }

        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }
}