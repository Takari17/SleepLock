package com.takari.sleeplock.di

import android.content.Context
import com.takari.sleeplock.sleeptimer.admin.AdminPermissionManager
import com.takari.sleeplock.sleeptimer.admin.DeviceSleeper
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    val adminPermission: AdminPermissionManager
    val deviceSleeper: DeviceSleeper

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}