package com.example.sleeplock.injection

import android.app.Application

class Application : Application() {

    companion object{
        lateinit var applicationComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory().create(applicationContext)
    }
}