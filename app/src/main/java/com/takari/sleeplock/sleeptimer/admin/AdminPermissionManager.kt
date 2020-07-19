package com.takari.sleeplock.sleeptimer.admin

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

/*
The API for checking the status of the admin permissions didn't suit my needs and so I created my
 own implementation.
 */
@Singleton
class AdminPermissionManager @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val adminReceiverComponentName: ComponentName
) {

    private val tag = "adminPermissions"

    /**Call startActivity with this intent to request the permission.*/
    val requestIntent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiverComponentName)
        putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Needed for the SleepTimer feature."
        )
    }

    fun setIsEnabled(isEnabled: Boolean) {
        sharedPrefs.edit { putBoolean(tag, isEnabled) }
    }

    fun isEnabled(): Boolean = sharedPrefs.getBoolean(tag, false)
}
