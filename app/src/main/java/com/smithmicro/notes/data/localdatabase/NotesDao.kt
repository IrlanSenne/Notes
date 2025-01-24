package com.smithmicro.notes.data.localdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smithmicro.notes.data.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes_list")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes_list WHERE noteId = :noteId")
    suspend fun getNoteById(noteId: String): NoteEntity

    @Query("DELETE FROM notes_list")
    suspend fun deleteAllNotes()
}
