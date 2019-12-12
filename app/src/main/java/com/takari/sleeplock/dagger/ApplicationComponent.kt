package com.takari.sleeplock.dagger

import android.content.Context
import com.takari.sleeplock.feature.sleeptimer.SleepTimerRepository
import com.takari.sleeplock.feature.sleeptimer.admin.AdminPermissions
import com.takari.sleeplock.feature.sleeptimer.admin.ScreenManager
import com.takari.sleeplock.feature.sleeptimer.admin.VolumeManager
import com.takari.sleeplock.feature.sleeptimer.ui.SleepTimerViewModel
import com.takari.sleeplock.feature.whitenoise.data.WhiteNoiseRepository
import com.takari.sleeplock.feature.whitenoise.ui.WhiteNoiseViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    val whiteNoiseViewModel: WhiteNoiseViewModel
    val sleepTimerViewModel: SleepTimerViewModel
    val whiteNoiseRepository: WhiteNoiseRepository
    val sleepTimerRepository: SleepTimerRepository
    val adminPermission: AdminPermissions
    val screenManager: ScreenManager
    val volumeManager: VolumeManager

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}