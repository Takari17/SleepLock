package com.takari.sleeplock.injection

import android.content.Context
import com.takari.sleeplock.data.Repository
import com.takari.sleeplock.data.local.SharedPrefs
import com.takari.sleeplock.data.SleepTimerService
import com.takari.sleeplock.ui.feature.SharedViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    val sharedViewModel: SharedViewModel
    val repository: Repository
    val sharedPrefs: SharedPrefs
    val sleepTimerService: SleepTimerService?

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}