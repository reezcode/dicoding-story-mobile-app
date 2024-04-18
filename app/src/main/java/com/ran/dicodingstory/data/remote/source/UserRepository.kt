package com.ran.dicodingstory.data.remote.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.google.gson.Gson
import com.ran.dicodingstory.data.remote.request.UserBody
import com.ran.dicodingstory.data.remote.response.DefaultResponse
import com.ran.dicodingstory.data.remote.response.ErrorResponse
import com.ran.dicodingstory.data.remote.response.Result
import com.ran.dicodingstory.data.remote.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository private constructor(
    private val apiService: ApiService,
){
    private val result = MediatorLiveData<Result<*>>()


    fun registerUser(name: String, email:String, password: String) : LiveData<Result<*>> {
        result.value = Result.Loading
        val user = UserBody(name = name, email = email, password = password)
        val client = apiService.register(user)
        client.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    result.value = Result.Success("Registration success! Please login")
                } else {
                    val gson = Gson()
                    val errorResponse = gson.fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                    val errorMessage = errorResponse.message
                    result.value = errorMessage.let { Result.Error(it) }
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                result.value = t.message?.let { Result.Error(it) }
            }
        })
        return result
    }

    fun loginUser(email: String, password: String) : LiveData<Result<*>> {
        result.value = Result.Loading
        val user = UserBody(email = email, password = password)
        val client = apiService.login(user)
        client.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    val loginResult = response.body()?.loginResult
                    result.value = loginResult?.let { Result.Success(it) }
                } else {
                    val gson = Gson()
                    val errorResponse = gson.fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                    val errorMessage = errorResponse.message
                    result.value = errorMessage.let { Result.Error(it) }
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                result.value = t.message?.let { Result.Error(it) }
            }
        })

        return result
    }

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        fun getInstance(apiService: ApiService): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserRepository(apiService)
                INSTANCE = instance
                instance
            }
        }
    }

}