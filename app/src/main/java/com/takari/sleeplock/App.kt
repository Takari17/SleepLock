package com.takari.sleeplock

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.takari.sleeplock.injection.ApplicationComponent
import com.takari.sleeplock.injection.DaggerApplicationComponent


class App : Application() {

    companion object {
        lateinit var applicationComponent: ApplicationComponent

        const val CHANNEL_ID = "custom channel if"

    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        applicationComponent = DaggerApplicationComponent.factory()
            .create(applicationContext)
    }

    //Notification Channel for API's above 26
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = getString(R.string.channel_description) }

            val notificationManager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            notificationManager.createNotificationChannel(channel)
        }
    }
}