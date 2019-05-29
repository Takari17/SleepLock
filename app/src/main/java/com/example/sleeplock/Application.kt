package com.example.sleeplock

import android.app.Application
import com.example.sleeplock.injection.ApplicationComponent
import com.example.sleeplock.injection.DaggerApplicationComponent

/*
Exposes Dagger component globally for classes I do not own (e.g Activities & Services)
 */
class Application : Application() {

    companion object{
        lateinit var applicationComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory()
            .create(applicationContext)
    }
}
