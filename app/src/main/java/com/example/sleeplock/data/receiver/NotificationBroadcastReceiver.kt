package com.example.sleeplock.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sleeplock.data.service.MainService

/*
 * Receives click events from the foreground notification buttons and triggers the onStartCommand
 * from MainService.
 */
class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Intent(context, MainService::class.java).also { serviceIntent ->
            //intent.action equals either play, pause or reset.
            serviceIntent.action = intent.action

            context.startService(serviceIntent)
        }
    }
}