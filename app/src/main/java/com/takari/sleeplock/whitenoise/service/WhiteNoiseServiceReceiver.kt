package com.takari.sleeplock.whitenoise.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receives broadcast from WhiteNoiseService's notification actions/buttons clicks. 
 * 
 * On click, the receiver takes the action (string) from the notification action/button clicked and
 * invokes the service's onStartCommand with an intent carrying the action.
 */
class WhiteNoiseServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Intent(context, WhiteNoiseService::class.java).apply {
            action = intent.action
            context.startService(this)
        }
    }
}
