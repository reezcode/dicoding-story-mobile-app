package com.ran.dicodingstory.ui.models

import androidx.lifecycle.ViewModel
import com.ran.dicodingstory.data.remote.source.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {

    fun newStory(imageFile: File, description: String, location : Pair<Double, Double>?) = storyRepository.newStory(imageFile, description, location)
}