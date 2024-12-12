package com.example.genggammakna.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.genggammakna.repository.ResultState
import com.example.genggammakna.repository.UserModel
import com.example.genggammakna.repository.UserRepository

class SignUpViewModel: ViewModel() {
    private val userRepository = UserRepository()

    private val _registerResult = MutableLiveData<ResultState<String>>()
    val registerResult: LiveData<ResultState<String>> = _registerResult

    fun registerUser(firstname: String, lastname: String, email: String, password: String){
        _registerResult.value = ResultState.Loading
        val user = UserModel(firstname = firstname, lastname = lastname, email = email, password = password)
        userRepository.registerUser(user){success, message ->
            if (success){
                _registerResult.postValue(ResultState.Success(message ?: "Registration successful"))
            } else {
                _registerResult.postValue(ResultState.Error(message ?: "Registration failed"))
            }
        }
    }
}