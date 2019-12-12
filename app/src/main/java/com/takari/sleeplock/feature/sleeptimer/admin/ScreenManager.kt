package com.takari.sleeplock.feature.sleeptimer.admin

import android.app.admin.DevicePolicyManager
import javax.inject.Inject

class ScreenManager @Inject constructor(
    private val policyManager: DevicePolicyManager
) {

    fun turnOffScreen() {
        policyManager.lockNow()
    }
}
