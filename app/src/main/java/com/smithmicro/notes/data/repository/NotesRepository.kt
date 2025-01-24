package com.smithmicro.notes.data.repository

import com.smithmicro.notes.data.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun deleteNote(note: NoteEntity)
    fun getNotesLocal(): Flow<List<NoteEntity>>
    suspend fun saveNote(note: NoteEntity)
    suspend fun saveNotesRemote(notes: List<NoteEntity>, userId: String)
    suspend fun getNoteById(noteId: String): NoteEntity
    suspend fun updateNoteLocal(note: NoteEntity)
    suspend fun deleteAllNotesLocal()

    suspend fun deleteAllNotesRemote(userId: String)
    suspend fun getNotesRemote(userId: String): Flow<List<NoteEntity>>
}