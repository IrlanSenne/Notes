package com.smithmicro.notes.usecases

import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.data.repository.NotesRepository

class GetNoteByIdUseCase(
    private val repository: NotesRepository
) : BaseUseCase<String, NoteEntity> {

    override suspend fun execute(input: String): NoteEntity {
        return repository.getNoteById(input)
    }
}
