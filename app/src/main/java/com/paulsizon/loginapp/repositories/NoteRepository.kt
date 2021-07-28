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
    suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.register(AccountRequest(email, password))
            if (response.isSuccessful) {
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.message(), null)
            }
        } catch (e: Exception) {
            Log.i("DebugApp", "AuthVM: $e")
            Resource.error(
                "Could not connect to the servers. Please check internet connection",
                null
            )
        }
    }
}