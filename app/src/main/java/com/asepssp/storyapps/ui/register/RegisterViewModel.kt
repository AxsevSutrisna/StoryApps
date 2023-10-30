package com.asepssp.storyapps.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asepssp.storyapps.data.database.repository.Result
import com.asepssp.storyapps.data.database.repository.UserRepository
import com.asepssp.storyapps.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registrationResult = MutableLiveData<Result<RegisterResponse>>()
    val registrationResult: LiveData<Result<RegisterResponse>> get() = _registrationResult

    fun registerAccount(name: String, email: String, password: String) {
        _registrationResult.value = Result.Loading

        viewModelScope.launch {
            val result = userRepository.registerUser(name, email, password)
            _registrationResult.value = result
        }
    }
}