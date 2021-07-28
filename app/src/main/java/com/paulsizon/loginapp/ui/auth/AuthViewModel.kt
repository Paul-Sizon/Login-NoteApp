package com.paulsizon.loginapp.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paulsizon.loginapp.other.Resource
import com.paulsizon.loginapp.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: NoteRepository) : ViewModel() {

    private val _registerStatus = MutableLiveData<Resource<String>>()
    val registerStatus: LiveData<Resource<String>> = _registerStatus


    fun register(email: String, password: String, repeatedPassword: String) {
        _registerStatus.postValue(Resource.loading(null))
        if (email.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
            _registerStatus.postValue(Resource.error("Please fill out all the fields", null))
            return
        }

        if (password != repeatedPassword) {
            _registerStatus.postValue(Resource.error("Passwords don't match", null))
            return
        }
        viewModelScope.launch {
            val result = repository.register(email, password)
            _registerStatus.postValue(result)
        }
    }
}