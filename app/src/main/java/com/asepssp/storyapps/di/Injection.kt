package com.asepssp.storyapps.di

import android.content.Context
import com.asepssp.storyapps.data.database.repository.UserRepository
import com.asepssp.storyapps.data.local.room.ListStoryDatabase
import com.asepssp.storyapps.data.remote.retrofit.ApiConfig
import com.asepssp.storyapps.pref.SettingPreference
import com.asepssp.storyapps.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val database = ListStoryDatabase.getDatabase(context)
        val pref = SettingPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(database, apiService, pref)
    }
}