package com.asepssp.storyapps.ui.storylocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.asepssp.storyapps.data.database.repository.Result
import com.asepssp.storyapps.data.database.repository.UserRepository
import com.asepssp.storyapps.data.remote.response.ListStoryItem

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    fun getListStoryWithLocation(): LiveData<Result<List<ListStoryItem>>> {
        return repository.getListStoryWithLocation()
    }
}