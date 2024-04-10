package com.ran.dicodingstory.ui.models

import androidx.lifecycle.ViewModel
import com.ran.dicodingstory.data.remote.source.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    fun registerUser(email: String, name:String, password: String) = userRepository.registerUser(email = email,name = name, password = password)
}