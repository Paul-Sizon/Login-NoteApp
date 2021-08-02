package com.paulsizon.loginapp.repositories

import android.app.Application
import android.util.Log
import com.paulsizon.loginapp.data.local.NoteDao
import com.paulsizon.loginapp.data.local.entities.Note
import com.paulsizon.loginapp.data.remote.NoteApi
import com.paulsizon.loginapp.data.remote.requests.AccountRequest
import com.paulsizon.loginapp.other.Resource
import com.paulsizon.loginapp.other.checkForInternetConnection
import com.paulsizon.loginapp.other.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApi: NoteApi,
    private val context: Application
) {
    fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkBoundResource(
            query = {
                noteDao.getAllNotes()
            },
            fetch = {
                noteApi.getNotes()
            },
            saveFetchResult = { response ->
                response.body()?.let {
                    //todo insert notes in db
                }
            },
            shouldFetch = {
                checkForInternetConnection(context)
            }
        )
    }

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
                Resource.success(response.body()?.message)
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