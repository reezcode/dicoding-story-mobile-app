package com.ran.dicodingstory.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DefaultResponse(
    val error: Boolean?,
    val message: String?,
    val loginResult: LoginResult?,
    val story: Story?,
    val listStory: List<Story>?,
) : Parcelable