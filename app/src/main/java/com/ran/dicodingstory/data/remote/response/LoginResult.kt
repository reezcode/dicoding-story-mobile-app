package com.ran.dicodingstory.data.remote.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResult(
    val userId: String?,
    val name: String?,
    val token: String?,
) : Parcelable
