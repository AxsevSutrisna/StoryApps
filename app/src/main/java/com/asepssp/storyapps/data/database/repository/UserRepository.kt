package com.asepssp.storyapps.data.database.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.asepssp.storyapps.data.local.room.ListStoryDatabase
import com.asepssp.storyapps.data.remote.response.ErrorResponse
import com.asepssp.storyapps.data.remote.response.ListStoryItem
import com.asepssp.storyapps.data.remote.response.LoginResponse
import com.asepssp.storyapps.data.remote.response.RegisterResponse
import com.asepssp.storyapps.data.remote.retrofit.ApiConfig
import com.asepssp.storyapps.data.remote.retrofit.ApiService
import com.asepssp.storyapps.pref.Model
import com.asepssp.storyapps.pref.SettingPreference
import com.asepssp.storyapps.pref.StoryRemoteMediator
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val database: ListStoryDatabase,
    private val apiService: ApiService,
    private val settingPreference: SettingPreference
) {

    suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Result<RegisterResponse> {
        Result.Loading
        return try {
            val response = apiService.userRegister(name, email, password)

            if (response.error == true) {
                Result.Error(response.message ?: "Unknown error")
            } else {
                Result.Success(response)
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Result.Error(errorMessage.toString())
        }
    }

    suspend fun loginUser(email: String, password: String): Result<LoginResponse> {
        Result.Loading
        return try {
            val response = apiService.userLogin(email, password)

            if (response.error) {
                Result.Error(response.message)
            } else {
                val session = Model(
                    name = response.loginResult.name,
                    email = email,
                    token = response.loginResult.token,
                    isLogin = true
                )
                saveSession(session)
                ApiConfig.token = response.loginResult.token
                Result.Success(response)
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Result.Error(errorMessage.toString())
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(database, apiService),
            pagingSourceFactory = {
                database.storyDao().getAllStory()
            }
        ).liveData
    }

    fun uploadStory(imageFile: File, description: String, lat: Double?, lon: Double?) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        val requestLat = lat?.toString()?.toRequestBody()
        val requestLon = lon?.toString()?.toRequestBody()
        try {
            val successResponse =
                apiService.uploadStory(multipartBody, requestBody, requestLat, requestLon)
            if (successResponse.error) {
                emit(Result.Error(successResponse.message))
            } else {
                emit(Result.Success(successResponse))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        }
    }

    fun getListStoryWithLocation(): LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getListStoryWithLocation()
            emit(Result.Success(response.listStory))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Result.Error(errorMessage.toString())
        }
    }


    private suspend fun saveSession(user: Model) {
        settingPreference.saveSession(user)
    }

    fun getSession(): Flow<Model> {
        return settingPreference.getSession()
    }

    suspend fun logout() {
        settingPreference.logout()
    }

    companion object {
        fun getInstance(
            database: ListStoryDatabase,
            apiService: ApiService,
            settingPreference: SettingPreference
        ): UserRepository = UserRepository(database, apiService, settingPreference)
    }

}