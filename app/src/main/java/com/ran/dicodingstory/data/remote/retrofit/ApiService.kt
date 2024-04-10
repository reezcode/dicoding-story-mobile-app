package com.ran.dicodingstory.data.remote.retrofit

import com.ran.dicodingstory.data.remote.response.DefaultResponse
import com.ran.dicodingstory.data.remote.request.UserBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    @Headers("UserResponse-Agent: request")
    fun register(@Body body: UserBody): Call<DefaultResponse>

    @POST("login")
    @Headers("UserResponse-Agent: request")
    fun login(@Body body: UserBody): Call<DefaultResponse>

    @Multipart
    @POST("stories")
    fun postStory(@Header("Authorization") token: String, @Part photo: MultipartBody.Part, @Part("description") description: RequestBody?, @Part("lat") lat: RequestBody?,
                  @Part("lon") lon: RequestBody?): Call<DefaultResponse>

    @GET("stories")
    @Headers("UserResponse-Agent: request", "Content-Type: multipart/form-data")
    suspend fun getAllStory(@Header("Authorization") token: String, @Query("page") page: Int? = 1, @Query("size") size: Int? = 10, @Query("location") location: Int? = 1): DefaultResponse
}