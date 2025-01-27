package com.smithmicro.notes.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_list")
data class NoteEntity(
    @PrimaryKey
    var noteId: String = "",
    var title: String = "",
    var content: String = "",
    val color: String = "#FFFFFFFF",
    val timestamp: Long = System.currentTimeMillis()
)