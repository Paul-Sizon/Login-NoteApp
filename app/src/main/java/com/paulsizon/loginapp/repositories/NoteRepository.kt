package com.paulsizon.loginapp.repositories

import android.app.Application
import android.util.Log
import com.paulsizon.loginapp.data.local.NoteDao
import com.paulsizon.loginapp.data.local.entities.LocallyDeletedNoteID
import com.paulsizon.loginapp.data.local.entities.Note
import com.paulsizon.loginapp.data.remote.NoteApi
import com.paulsizon.loginapp.data.remote.requests.AccountRequest
import com.paulsizon.loginapp.data.remote.requests.AddOwnerRequest
import com.paulsizon.loginapp.data.remote.requests.DeleteNoteRequest
import com.paulsizon.loginapp.other.Resource
import com.paulsizon.loginapp.other.checkForInternetConnection
import com.paulsizon.loginapp.other.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApi: NoteApi,
    private val context: Application
) {
    suspend fun insertNote(note: Note) {
        val response = try {
            noteApi.addNote(note)
        } catch (e: Exception) {
            null
        }
        if (response != null && response.isSuccessful) {
            noteDao.insertNote(note.apply { isSynced = true })
        } else {
            //isSynced is false by default
            noteDao.insertNote(note)
        }
    }

    suspend fun insertNotes(notes: List<Note>) {
        notes.forEach { insertNote(it) }
    }

    suspend fun deleteNote(noteID: String) {
        val response = try {
            noteApi.deleteNote(DeleteNoteRequest(noteID))
        } catch (e: Exception){
            null
        }
        noteDao.deleteNoteById(noteID)
        if (response == null || !response.isSuccessful){
            noteDao.insertLocallyDeletedNoteId(LocallyDeletedNoteID(noteID))
        } else {
            deleteLocallyDeletedNoteID(noteID)
        }
    }

    fun observeNoteById(noteId: String) = noteDao.observeNoteById(noteId)

    suspend fun deleteLocallyDeletedNoteID(deletedNoteID: String) {
        noteDao.deleteLocallyDeletedNoteIDs(deletedNoteID)
    }

    suspend fun getNoteById(noteId: String) = noteDao.getNoteById(noteId)

    private var curNotesReponse: Response<List<Note>>? = null

    suspend fun syncNotes(){
//        get locally deleted notes
        val locallyDeletedNoteIDs = noteDao.getAllLocallyDeletedNoteIDs()
//       delete them on server
        locallyDeletedNoteIDs.forEach { id ->
            deleteNote(id.deletedNoteID)
        }
//      upload unsync notes to server
        val unSyncedNotes = noteDao.getAllUnSyncNotes()
        unSyncedNotes.forEach { note -> insertNote(note) }
//      get notes from server
        curNotesReponse = noteApi.getNotes()
//      delete notes locally and get them from server with IsSync being true
        curNotesReponse?.body()?.let { notes ->
            noteDao.deleteAllNotes()
            insertNotes(notes.onEach { note -> note.isSynced = true })
        }
    }

    fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkBoundResource(
            query = {
                noteDao.getAllNotes()
            },
            fetch = {
                syncNotes()
                curNotesReponse
            },
            saveFetchResult = { response ->
                response?.body()?.let {
                    insertNotes(it.onEach { note -> note.isSynced = true })
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
    suspend fun addOwnerToNote(owner: String, noteId: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.addOwnerToNote(AddOwnerRequest(owner, noteId))
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