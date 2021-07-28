package com.paulsizon.loginapp.repositories

import android.app.Application
import android.util.Log
import com.paulsizon.loginapp.data.local.NoteDao
import com.paulsizon.loginapp.data.remote.NoteApi
import com.paulsizon.loginapp.data.remote.requests.AccountRequest
import com.paulsizon.loginapp.other.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApi: NoteApi,
    private val context: Application
) {
    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.login(AccountRequest(email, password))
            if (response.isSuccessful && response.body()!!.succeful) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch (e: Exception) {
            Log.i("DebugApp", "Login: $e")
            Resource.error(
                "Could not connect to the servers. Please check internet connection",
                null
            )
        }
    }
    suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.register(AccountRequest(email, password))
            if (response.isSuccessful && response.body()!!.succeful) {
                Resource.success(response.body()?.message )
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch (e: Exception) {
            Log.i("DebugApp", "Register: $e")
            Resource.error(
                "Could not connect to the servers. Please check internet connection",
                null
            )
        }
    }
}