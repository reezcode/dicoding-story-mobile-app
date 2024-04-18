package com.ran.dicodingstory.data.remote.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.ran.dicodingstory.data.local.entity.StoryResponseItem
import com.ran.dicodingstory.data.local.room.StoryDatabase
import com.ran.dicodingstory.data.local.source.UserPreferencesRepository
import com.ran.dicodingstory.data.remote.response.ErrorResponse
import com.ran.dicodingstory.data.remote.response.Result
import com.ran.dicodingstory.data.remote.retrofit.ApiService
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val apiService: ApiService,
) {
    private val result = MediatorLiveData<Result<*>>()

    fun newStory(imageFile: File, description: String, location: Pair<Double, Double>?) : LiveData<Result<*>> {
        result.value = Result.Loading
        val token = runBlocking { userPreferencesRepository.getToken().getOrNull().orEmpty() }
        val descriptionBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        var latBody: RequestBody? = null
        var lonBody: RequestBody? = null
        if(location != null) {
            latBody = location.first.toString().toRequestBody("text/plain".toMediaType())
            lonBody = location.second.toString().toRequestBody("text/plain".toMediaType())
        }
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val client = apiService.postStory(token = "Bearer $token", photo = multipartBody, description = descriptionBody, lat = latBody, lon = lonBody)
        client.enqueue(object : retrofit2.Callback<com.ran.dicodingstory.data.remote.response.DefaultResponse> {
            override fun onResponse(call: retrofit2.Call<com.ran.dicodingstory.data.remote.response.DefaultResponse>, response: retrofit2.Response<com.ran.dicodingstory.data.remote.response.DefaultResponse>) {
                if (response.isSuccessful) {
                    result.value = Result.Success("Story uploaded successfully")
                } else {
                    val gson = Gson()
                    val errorResponse = gson.fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                    val errorMessage = errorResponse.message
                    result.value = errorMessage.let { Result.Error(it) }
                }
            }
            override fun onFailure(call: retrofit2.Call<com.ran.dicodingstory.data.remote.response.DefaultResponse>, t: Throwable) {
                result.value = t.message?.let { Result.Error(it) }
            }
        })
        return result
    }

    fun getStories() : LiveData<Result<*>> {
        result.value = Result.Loading
        val token = runBlocking { userPreferencesRepository.getToken().getOrNull().orEmpty() }
        val client = apiService.getStoriesWithLocation(token = "Bearer $token")
        client.enqueue(object : retrofit2.Callback<com.ran.dicodingstory.data.remote.response.DefaultResponse> {
            override fun onResponse(call: retrofit2.Call<com.ran.dicodingstory.data.remote.response.DefaultResponse>, response: retrofit2.Response<com.ran.dicodingstory.data.remote.response.DefaultResponse>) {
                if (response.isSuccessful) {
                    result.value = Result.Success(response.body())
                } else {
                    val gson = Gson()
                    val errorResponse = gson.fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                    val errorMessage = errorResponse.message
                    result.value = errorMessage.let { Result.Error(it) }
                }
            }
            override fun onFailure(call: retrofit2.Call<com.ran.dicodingstory.data.remote.response.DefaultResponse>, t: Throwable) {
                result.value = t.message?.let { Result.Error(it) }
            }
        })
        return result
    }

    fun getStoriesPaging(): LiveData<PagingData<StoryResponseItem>> {
        val token = runBlocking { userPreferencesRepository.getToken().getOrNull().orEmpty() }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator("Bearer $token", storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(storyDatabase: StoryDatabase,userPreferencesRepository: UserPreferencesRepository,apiService: ApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, userPreferencesRepository,apiService).apply { instance = this }
            }
    }
}