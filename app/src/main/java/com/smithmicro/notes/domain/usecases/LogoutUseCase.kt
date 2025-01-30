package com.smithmicro.notes.domain.usecases

import android.content.Context
import com.smithmicro.notes.data.repository.AuthRepository
import com.smithmicro.notes.data.repository.NotesRepository
import com.smithmicro.notes.utils.isInternetAvailable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val notesRepository: NotesRepository,
    @ApplicationContext private val context: Context
) : BaseUseCase<Unit, Flow<Boolean>> {

    override suspend fun execute(input: Unit): Flow<Boolean> = flow {
        try {
            if (isInternetAvailable(context)) {
                val currentUser = authRepository.currentUser
                val localNotes = notesRepository.getNotesLocal().first()

                currentUser?.email?.let { notesRepository.deleteAllNotesRemote(it) }
                currentUser?.email?.let { notesRepository.saveNotesRemote(localNotes, it) }

                notesRepository.deleteAllNotesLocal()
                authRepository.logout()
                emit(true)
            } else {
                throw Exception("No internet connection")
            }
        } catch (e: Exception) {
            emit(false)
        }
    }
}
