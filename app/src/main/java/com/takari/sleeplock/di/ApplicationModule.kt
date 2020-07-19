package com.takari.sleeplock.di

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.preference.PreferenceManager
import com.takari.sleeplock.sleeptimer.admin.SleepTimerAdminReceiver
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