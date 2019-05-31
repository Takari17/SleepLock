package com.example.sleeplock

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.sleeplock.injection.ApplicationComponent
import com.example.sleeplock.injection.DaggerApplicationComponent
import com.example.sleeplock.utils.CHANNEL_ID

/*
Exposes Dagger component globally for classes I do not own (e.g Activities & Services)
 */
class Application : Application() {

    companion object {
        lateinit var applicationComponent: ApplicationComponent
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