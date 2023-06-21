package com.takari.sleeplock.sleeptimer.permissions

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.takari.sleeplock.sleeptimer.admin.SleepTimerAdminReceiver
import javax.inject.Inject

/*
The API for checking the status of the admin permissions was difficult to deal with,
so I'm just using shared preferences to save a simple 'isGranted' boolean that's modified
in SleepTimerAdminReceiver.kt .
 */
class AdminPermissionManager @Inject constructor(
    private val sharedPrefs: SharedPreferences,
) {

    private val tag = "adminPermissions"

    fun requestPermissions(fragment: Fragment, isGranted: (Boolean) -> Unit) {

        val requestPermissionLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == 0) {
                isGranted(false)
            } else {
                isGranted(true)
            }
        }

        val adminRequestIntent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                ComponentName(fragment.requireContext(), SleepTimerAdminReceiver::class.java)
            )

            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "This permission is needed in order to sleep your device when the " +
                        "timer reaches zero. This functionality is the same as pressing your " +
                        "power button once."
            )
        }

        requestPermissionLauncher.launch(adminRequestIntent)
    }

    fun setPermissionIsEnabled(isEnabled: Boolean) {
        sharedPrefs.edit { putBoolean(tag, isEnabled) }
    }

    fun permissionIsEnabled(): Boolean = sharedPrefs.getBoolean(tag, false)
}
