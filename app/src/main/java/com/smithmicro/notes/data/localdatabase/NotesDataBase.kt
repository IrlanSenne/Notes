package com.smithmicro.notes.data.localdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smithmicro.notes.data.entities.NoteEntity

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}