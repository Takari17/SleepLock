package com.takari.sleeplock.feature.sleeptimer.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.takari.sleeplock.App.Companion.applicationComponent

/*
DeviceAdminReceiver extents BroadcastReceiver. It's intuitive, onDisabled is called when the
user disables the admin permission and vice versa with onEnabled.
 */
class SleepTimerAdminReceiver : DeviceAdminReceiver() {

    private val adminPermission = applicationComponent.adminPermission

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        adminPermission.save(true)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        adminPermission.save(false)
    }
}
