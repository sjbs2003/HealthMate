package com.example.healthmate.viewmodel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get(), get()) }
    viewModel { ProductViewModel(get()) }
    viewModel { CartViewModel(get()) }
}