package com.example.healthmate.viewmodel

import androidx.lifecycle.ViewModel
import com.example.healthmate.model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

}