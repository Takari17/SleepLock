package com.takari.sleeplock.dagger

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.takari.sleeplock.feature.common.ButtonStateColor
import com.takari.sleeplock.feature.common.Timer
import com.takari.sleeplock.feature.sleeptimer.admin.SleepTimerAdminReceiver
import com.takari.sleeplock.feature.sleeptimer.ui.SleepTimerViewState
import com.takari.sleeplock.feature.whitenoise.ui.WhiteNoiseViewState
import dagger.Module
import dagger.Provides

@Module
object ApplicationModule {

    @JvmStatic
    @Provides
    fun sharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)


    @JvmStatic
    @Provides
    fun gson(): Gson = Gson()


    @JvmStatic
    @Provides
    fun defaultWhiteNoiseViewState(): WhiteNoiseViewState =
        WhiteNoiseViewState(
            currentTime = 0,
            whiteNoise = null,
            color = ButtonStateColor.Disabled,
            isEnabled = false,
            timerAction = Timer.Action.Start
        )

    @JvmStatic
    @Provides
    fun defaultSleepTimerViewState(): SleepTimerViewState =
        SleepTimerViewState(
            currentTime = 0,
            color = ButtonStateColor.Disabled,
            isEnabled = false,
            timerAction = Timer.Action.Start
        )

    @JvmStatic
    @Provides
    fun audioManager(context: Context): AudioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    @JvmStatic
    @Provides
    fun devicePolicyManager(context: Context): DevicePolicyManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    @JvmStatic
    @Provides
    fun sleepLockAdminReceiverComponentName(context: Context): ComponentName =
        ComponentName(context, SleepTimerAdminReceiver::class.java)

}