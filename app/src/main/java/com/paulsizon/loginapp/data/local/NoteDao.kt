package com.paulsizon.loginapp.data.local

import android.text.TextUtils.replace
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paulsizon.loginapp.data.local.entities.Note
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :noteID")
    suspend fun deleteNoteById(noteID: String)

    @Query("DELETE FROM notes WHERE isSynced = 1")
    suspend fun deleteSyncNotes()

    // not suspend function beacuse of livedata
    @Query("SELECT * FROM notes WHERE id =:noteID")
    fun observeNoteById(noteID: String): LiveData<Note>

    @Query("SELECT * FROM notes WHERE id =:noteID")
    suspend fun getNoteById(noteID: String): Note?

    //flow to make it easier to cache
    @Query("SELECT * FROM notes ORDER BY date DESC" )
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getAllUnSyncNotes(): List<Note>


}