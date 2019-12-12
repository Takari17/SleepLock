package com.takari.sleeplock.feature.sleeptimer.admin

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

/*
The API for checking the status of the admin permissions didn't suit my needs (it only updates when you reset
the app) and so I created my own way of checking the status.
 */
@Singleton
class AdminPermissions @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    private val adminReceiverComponentName: ComponentName
) {

    /**
     * Call startActivity with this intent to request the permission.
     */
    val requestIntent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
        putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiverComponentName)
        putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            "Needed for the SleepTimer feature."
        )
    }

    fun save(isEnabled: Boolean) {
        sharedPrefs.edit { putBoolean("adminPermission", isEnabled) }
    }

    fun status(): Boolean = sharedPrefs.getBoolean("adminPermission", false)
}
