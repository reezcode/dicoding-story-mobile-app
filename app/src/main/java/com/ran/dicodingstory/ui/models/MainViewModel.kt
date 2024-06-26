package com.ran.dicodingstory.ui.models

import androidx.lifecycle.ViewModel
import com.ran.dicodingstory.data.remote.source.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (private val storyRepository: StoryRepository) : ViewModel() {

    fun getStoriesPaging() = storyRepository.getStoriesPaging()


}