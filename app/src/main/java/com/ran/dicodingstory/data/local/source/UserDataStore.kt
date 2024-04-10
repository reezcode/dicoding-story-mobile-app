package com.ran.dicodingstory.data.local.source


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "com.ran.dicodingstory.user_preferences"
)