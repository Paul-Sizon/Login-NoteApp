package com.paulsizon.loginapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.util.*

@Entity(tableName = "notes")
data class Note(
    val title: String,
    val content: String,
    val date: Long,
    val owners: List<String>,
    val color: String,
    //to be ignored by retrofit
    @Expose(deserialize = false, serialize = false)
    var isSynced: Boolean = false,
    //id key is already generated on server side
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString()
)