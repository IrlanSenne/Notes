package com.smithmicro.notes.domain.usecases

import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.data.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GetNotesUseCase(
    private val repository: NotesRepository
) : BaseUseCase<Unit, Flow<List<NoteEntity>>> {

    override suspend fun execute(input: Unit): Flow<List<NoteEntity>> = flow {
        try {
            val localNotes = repository.getNotesLocal().first()
            emit(localNotes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
