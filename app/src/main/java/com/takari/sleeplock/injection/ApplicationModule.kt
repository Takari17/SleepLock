package com.takari.sleeplock.injection

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.takari.sleeplock.data.SleepTimerService
import dagger.Module
import dagger.Provides

@Module
object ApplicationModule {

    @JvmStatic
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @JvmStatic
    @Provides
    fun providesTimerService(): SleepTimerService =
        SleepTimerService()
}