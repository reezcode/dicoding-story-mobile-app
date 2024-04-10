package com.ran.dicodingstory.ui.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ran.dicodingstory.data.local.source.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(private val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    private val _session = MutableLiveData<Boolean?>()
    val session : LiveData<Boolean?> = _session

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        viewModelScope.launch { getSession() }
    }

    private suspend fun getSession() {
        coroutineScope.launch {
            val session = userPreferencesRepository.getSession().getOrNull()
            _session.postValue(session)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}