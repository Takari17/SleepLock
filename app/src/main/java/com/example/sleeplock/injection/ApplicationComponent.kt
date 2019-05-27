package com.example.sleeplock.injection

import android.content.Context
import com.example.sleeplock.data.Repository
import com.example.sleeplock.data.local.SharedPrefs
import com.example.sleeplock.ui.viewmodel.MainViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    val mainViewModel: MainViewModel
    val repository: Repository
    val sharedPrefs: SharedPrefs

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}