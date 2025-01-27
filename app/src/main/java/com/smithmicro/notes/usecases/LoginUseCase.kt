package com.smithmicro.notes.usecases

import android.content.Context
import com.smithmicro.notes.data.repository.AuthRepository
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.data.entities.UserEntity
import com.smithmicro.notes.data.repository.NotesRepository
import com.smithmicro.notes.utils.isInternetAvailable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val notesRepository: NotesRepository,
    @ApplicationContext private val context: Context
) : BaseUseCase<UserEntity, Flow<Resource<List<NoteEntity>>>> {

    override suspend fun execute(userEntity: UserEntity): Flow<Resource<List<NoteEntity>>> = flow {
        emit(Resource.Loading)

        try {
            if (isInternetAvailable(context)) {
                authRepository.login(userEntity.email, userEntity.password)

                val currentUser = authRepository.currentUser
                val userId = currentUser?.email

                if (userId != null) {
                    val notes = mutableListOf<NoteEntity>()

                    notesRepository.getNotesRemote(userId).firstOrNull()?.let { remoteNotes ->
                        remoteNotes.forEach { note ->
                            notesRepository.saveNote(note)
                            notes.add(note)
                        }
                    }

                    emit(Resource.Success(notes))
                } else {
                    emit(Resource.Failure(Exception("User ID not found")))
                }
            } else {
                emit(Resource.Failure(Exception("No internet connection")))
            }
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }
}
