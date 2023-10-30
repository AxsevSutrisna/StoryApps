package com.asepssp.storyapps.ui.addstory

import androidx.lifecycle.ViewModel
import com.asepssp.storyapps.data.database.repository.UserRepository
import java.io.File

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    fun uploadStory(file: File, description: String, lat: Double? = null, lon: Double? = null) =
        repository.uploadStory(file, description, lat, lon)
}