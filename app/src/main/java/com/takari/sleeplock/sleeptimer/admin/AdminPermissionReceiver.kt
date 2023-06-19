package com.takari.sleeplock.sleeptimer.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import javax.inject.Inject


/**
DeviceAdminReceiver extents BroadcastReceiver. After requesting admin permissions one of these
callbacks will be called.
 */
class AdminPermissionReceiver @Inject constructor(
    private val adminPermission: AdminPermissionManager
) : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        adminPermission.setIsEnabled(true)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        adminPermission.setIsEnabled(false)
    }
}
