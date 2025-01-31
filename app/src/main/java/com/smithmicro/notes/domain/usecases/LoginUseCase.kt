package com.smithmicro.notes.domain.usecases

import android.content.Context
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.smithmicro.notes.R
import com.smithmicro.notes.data.repository.AuthRepository
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.data.entities.UserEntity
import com.smithmicro.notes.data.exception.AuthException
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
            if (!isInternetAvailable(context)) {
                throw AuthException.NoInternetConnectionException()
            }

            val result = authRepository.login(userEntity.email, userEntity.password)

            if (result is Resource.Failure) {
                val errorMessageResId = when (result.exception) {
                    is FirebaseAuthInvalidCredentialsException -> R.string.error_incorrect_credentials
                    is FirebaseAuthInvalidUserException -> R.string.error_user_not_found
                    is AuthException.InvalidEmailException -> R.string.error_invalid_email
                    is AuthException.InvalidPasswordException -> R.string.error_invalid_password
                    is AuthException.NoInternetConnectionException -> R.string.error_no_internet_connection
                    else -> R.string.error_unknown
                }

                throw AuthException.CustomAuthException(errorMessageResId) // Passando o ID correto
            }

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
                throw AuthException.UserNotFoundException()
            }
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }
}

