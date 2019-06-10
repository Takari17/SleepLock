package com.takari.sleeplock.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.takari.sleeplock.data.service.MainService

/*
 * Receives click events from the MainService.kt notification actions and triggers it's onStartCommand method
 * passing an intent with the action of the button clicked.
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {

    companion object {
        fun createIntent(context: Context, action: String) =
            Intent(context, NotificationBroadcastReceiver::class.java).apply {
                this.action = action
            }
    }

    override fun onReceive(context: Context, intent: Intent) {

        Intent(context, MainService::class.java).also { serviceIntent ->
            //intent.action only set to IntentAction constants in DataSource file
            serviceIntent.action = intent.action

            context.startService(serviceIntent)
        }
    }
}