package com.smithmicro.notes.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.data.localdatabase.NoteDao
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val firebaseFirestore: FirebaseFirestore
) : NotesRepository {

    override suspend fun deleteNote(note: NoteEntity) {
        try {
            noteDao.deleteNote(note)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getNotesLocal(): Flow<List<NoteEntity>> {
        return try {
            noteDao.getAllNotes()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllNotesLocal() {
        try {
            noteDao.deleteAllNotes()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveNote(note: NoteEntity) {
        try {
            val existingNote = noteDao.getNoteById(note.noteId)
            if (existingNote == null) {
                noteDao.insertNote(note)
            } else {
                noteDao.updateNote(note)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getNoteById(noteId: String): NoteEntity {
        return noteDao.getNoteById(noteId)
    }

    override suspend fun updateNoteLocal(note: NoteEntity) {
        try {
            noteDao.updateNote(note)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getNotesRemote(userId: String): Flow<List<NoteEntity>> = callbackFlow {
        val notesCollection = firebaseFirestore.collection(userId)
        val listener = notesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val notes = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(NoteEntity::class.java)
            } ?: emptyList()

            trySend(notes).isSuccess
        }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun saveNotesRemote(notes: List<NoteEntity>, userId: String) {
        try {
            val notesCollection = firebaseFirestore.collection(userId)

            notes.forEach { note ->
                val noteData = hashMapOf(
                    "title" to note.title,
                    "content" to note.content,
                    "timestamp" to note.timestamp,
                    "noteId" to note.noteId
                )
                notesCollection.document(note.noteId).set(noteData).await()
            }

        } catch (_: Exception) {}
    }

    override suspend fun deleteAllNotesRemote(userId: String) {
        val notesCollection = firebaseFirestore.collection(userId)

        try {
            val documents = notesCollection.get().await()

            documents.forEach {
                notesCollection.document(it.id).delete().await()
            }

        } catch (e: Exception) {
            throw e
        }
    }
}