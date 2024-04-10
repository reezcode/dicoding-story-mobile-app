package com.ran.dicodingstory.ui.models.events

import androidx.paging.PagingData
import com.ran.dicodingstory.data.remote.response.Story

sealed class MainEvent {
    data object Loading : MainEvent()
    data object Complete : MainEvent()
    class Success(
        val data: PagingData<Story>
    ) : MainEvent()
    class Failed(
        val e: String?
    ) : MainEvent()
}