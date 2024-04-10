package com.ran.dicodingstory.data.remote.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.gson.Gson
import com.ran.dicodingstory.data.local.source.UserPreferencesRepository
import com.ran.dicodingstory.data.remote.response.ErrorResponse
import com.ran.dicodingstory.data.remote.response.Result
import com.ran.dicodingstory.data.remote.retrofit.ApiService
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val apiService: ApiService,
) {
    private val result = MediatorLiveData<Result<*>>()

    fun newStory(imageFile: File, description: String, location: Pair<Double, Double>) : LiveData<Result<*>> {
        result.value = Result.Loading
        val token = runBlocking { userPreferencesRepository.getToken().getOrNull().orEmpty() }
        val descriptionBody = description.toRequestBody("text/plain".toMediaType())
        val latBody = location.first.toString().toRequestBody("text/plain".toMediaType())
        val lonBody = location.second.toString().toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
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

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(userPreferencesRepository: UserPreferencesRepository,apiService: ApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(userPreferencesRepository,apiService).apply { instance = this }
            }
    }
}