package com.takari.sleeplock.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        const val CHANNEL_ID = "=channel id"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

        // Only needed for API 26 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "SleepLock Notification",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Displays Current Time"

            val notificationManager =
                (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)

            notificationManager.createNotificationChannel(channel)
        }
    }
}
