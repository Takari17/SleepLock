package com.takari.sleeplock.sleeptimer.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import com.takari.sleeplock.sleeptimer.permissions.AdminPermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
Called when user either enables or disables admin permissions. DeviceAdminReceiver
extents BroadcastReceiver.
 */
@AndroidEntryPoint
class SleepTimerAdminReceiver : DeviceAdminReceiver() {

    @Inject
    lateinit var permissionManager: AdminPermissionManager

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        permissionManager.setPermissionIsEnabled(true)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        permissionManager.setPermissionIsEnabled(false)
    }
}
