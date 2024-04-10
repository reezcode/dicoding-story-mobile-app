package com.ran.dicodingstory.ui.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ran.dicodingstory.data.local.source.UserPreferencesRepository
import com.ran.dicodingstory.data.remote.source.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository, private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {
    private var viewModelJob = Job()

    fun login(email: String, password: String) = userRepository.loginUser(email = email, password = password)

    fun saveToken(token: String) {
        viewModelScope.launch {
            userPreferencesRepository.setToken(token)
        }
    }

    fun saveSession(session: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setSession(session)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
