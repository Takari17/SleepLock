package com.takari.sleeplock

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

import com.takari.sleeplock.dagger.ApplicationComponent
import com.takari.sleeplock.dagger.DaggerApplicationComponent

/*
todo we gotta go through all of our classes and fix the fomratting, amke everything presentable.
 */
class App : Application() {


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        applicationComponent = DaggerApplicationComponent.factory()
            .create(applicationContext)
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API 26 and above

            val channel = NotificationChannel(
                CHANNEL_ID,
                "SleepLock Notification",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Displays Current Time"

            val notificationManager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        lateinit var applicationComponent: ApplicationComponent
        const val CHANNEL_ID = "custom channel if"
    }
}
