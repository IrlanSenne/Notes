package com.smithmicro.notes.usecases

import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.data.repository.NotesRepository

class SaveNoteUseCase(
    private val repository: NotesRepository
) : BaseUseCase<NoteEntity, Unit> {

    override suspend fun execute(input: NoteEntity) {
        try {
            repository.saveNote(input)
        } catch (e: Exception) {
            throw e
        }
    }
}