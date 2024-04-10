package com.ran.dicodingstory.ui.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ran.dicodingstory.data.local.source.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val userPreferencesRepository: UserPreferencesRepository) :ViewModel() {
    private var viewModelJob = Job()
    fun deleteToken() {
        viewModelScope.launch {
            userPreferencesRepository.deleteToken()
            userPreferencesRepository.deleteSession()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}