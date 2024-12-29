package com.example.healthmate

import android.app.Application
import com.example.healthmate.model.AppContainer
import com.example.healthmate.model.DefaultAppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HealthMateApplication: Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}