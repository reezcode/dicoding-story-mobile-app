package com.ran.dicodingstory.data.remote.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String
)
