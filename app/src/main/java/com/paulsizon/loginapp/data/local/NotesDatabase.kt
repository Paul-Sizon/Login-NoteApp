package com.paulsizon.loginapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paulsizon.loginapp.data.local.entities.LocallyDeletedNoteID
import com.paulsizon.loginapp.data.local.entities.Note


@Database(
    entities = [Note::class, LocallyDeletedNoteID::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class NotesDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao

}