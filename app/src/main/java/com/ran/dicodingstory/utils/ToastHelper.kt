package com.ran.dicodingstory.utils

import android.content.Context
import android.widget.Toast

object ToastHelper {
    private var applicationContext: Context? = null

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        applicationContext?.let {
            Toast.makeText(it, message, duration).show()
        }
    }
}