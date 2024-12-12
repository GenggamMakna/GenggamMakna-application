package com.example.genggammakna.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.genggammakna.preferences.UserPreferences
import com.example.genggammakna.repository.ResultState
import com.example.genggammakna.repository.UserRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository()
    private val userPreferences = UserPreferences(application)

    private val _loginResult = MutableLiveData<ResultState<String>>()
    val loginResult: LiveData<ResultState<String>> = _loginResult

    fun loginUser(email: String, password: String) {
        _loginResult.value = ResultState.Loading
        userRepository.loginUser(email, password) { success, message, user ->
            if (success) {
                user?.let {
                    userPreferences.saveUser(it)
                }
                _loginResult.postValue(ResultState.Success(message ?: "Login successful"))
            } else {
                _loginResult.postValue(ResultState.Error(message ?: "Login Failed"))
            }
        }
    }
}
