package com.paulsizon.loginapp.ui.notes

import androidx.lifecycle.*
import com.paulsizon.loginapp.data.local.entities.LocallyDeletedNoteID
import com.paulsizon.loginapp.data.local.entities.Note
import com.paulsizon.loginapp.other.Event
import com.paulsizon.loginapp.other.Resource
import com.paulsizon.loginapp.repositories.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _allNotes = _forceUpdate.switchMap {
        repository.getAllNotes().asLiveData(viewModelScope.coroutineContext)
    }.switchMap {
        MutableLiveData(Event(it))
    }
    val allNotes: LiveData<Event<Resource<List<Note>>>> = _allNotes

    fun syncAllNotes() = _forceUpdate.postValue(true)

    fun insertNote(note: Note) = viewModelScope.launch {
        repository.insertNote(note)
    }

    fun deleteNote(noteId:String) = viewModelScope.launch {
        repository.deleteNote(noteId)
    }

    fun deleteLocallyDeletedNoteID(deletedNoteID: String) = viewModelScope.launch {
        repository.deleteLocallyDeletedNoteID(deletedNoteID)
    }
}