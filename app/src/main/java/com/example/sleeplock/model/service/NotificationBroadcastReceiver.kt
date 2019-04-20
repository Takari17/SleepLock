package com.example.sleeplock.model.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.sleeplock.model.util.Constants

// Receives click events from the foreground notification button
class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val actionIntent = Intent(context, CustomService::class.java)

        when (action) {
            Constants.ACTION_PLAY.text -> {
                actionIntent.action = action
                context.startService(actionIntent)
            }

            Constants.ACTION_PAUSE.text -> {
                actionIntent.action = action
                context.startService(actionIntent)
            }

            Constants.ACTION_RESET.text -> {
                actionIntent.action = action
                context.startService(actionIntent)
            }
        }
    }
}