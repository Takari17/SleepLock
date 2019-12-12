package com.takari.sleeplock.feature.whitenoise.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WhiteNoiseServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Intent(context, WhiteNoiseService::class.java).also { serviceIntent ->
            serviceIntent.action = intent.action
            context.startService(serviceIntent)
        }
    }

    companion object {
        fun createIntent(context: Context, action: String) =
            Intent(context, WhiteNoiseServiceReceiver::class.java).apply {
                this.action = action
            }
    }
}
