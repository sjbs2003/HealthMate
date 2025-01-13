package com.example.healthmate.model

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { Repository(get()) }
}

val authModule = module {
    single { AuthManager(androidContext()) }
}