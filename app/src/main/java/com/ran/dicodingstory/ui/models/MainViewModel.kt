package com.ran.dicodingstory.ui.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ran.dicodingstory.data.local.source.UserPreferencesRepository
import com.ran.dicodingstory.data.remote.retrofit.ApiService
import com.ran.dicodingstory.data.remote.source.StoryPagingSource
import com.ran.dicodingstory.ui.models.events.MainEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (private val userPreferencesRepository: UserPreferencesRepository, private val apiService: ApiService) : ViewModel() {
    private val _isFailed = MutableLiveData<Boolean>()
    val isFailed : LiveData<Boolean> = _isFailed

    fun getStories() = flow {
        emit(MainEvent.Loading)
        _isFailed.value = false
        val token = userPreferencesRepository.getToken().getOrNull().orEmpty()
        val stories = Pager(PagingConfig(pageSize = 20, enablePlaceholders = true)) {
            StoryPagingSource(token, apiService) {
                _isFailed.value = true
            }
        }.flow.cachedIn(viewModelScope).firstOrNull()
        emit(stories?.let { MainEvent.Success(it) })
    }

}