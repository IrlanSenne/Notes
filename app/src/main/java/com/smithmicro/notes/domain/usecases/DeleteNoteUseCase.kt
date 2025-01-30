package com.smithmicro.notes.domain.usecases

import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.data.repository.NotesRepository

class DeleteNoteUseCase(
    private val repository: NotesRepository
) : BaseUseCase<NoteEntity, Unit> {
    override suspend fun execute(input: NoteEntity) {
        repository.deleteNote(input)
    }
}
