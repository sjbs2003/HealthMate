package com.example.healthmate

import android.app.Application
import com.example.healthmate.model.authModule
import com.example.healthmate.model.networkModule
import com.example.healthmate.model.repositoryModule
import com.example.healthmate.viewmodel.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class HealthMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@HealthMateApplication)
            modules(
                networkModule,
                repositoryModule,
                authModule,
                viewModelModule
            )
        }
    }
}