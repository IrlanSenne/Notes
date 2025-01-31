package com.smithmicro.notes.domain.usecases

import android.content.Context
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.smithmicro.notes.R
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.data.repository.AuthRepository
import com.smithmicro.notes.data.entities.UserEntity
import com.smithmicro.notes.data.exception.AuthException
import com.smithmicro.notes.utils.isInternetAvailable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SignupUseCase(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : BaseUseCase<UserEntity, Flow<Resource<Boolean>>> {

    override suspend fun execute(userEntity: UserEntity): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)

        try {
            if (!isInternetAvailable(context)) {
                throw AuthException.NoInternetConnectionException()
            }

            // Tenta realizar o cadastro
            val result = authRepository.signup(userEntity.name ?: "", userEntity.email, userEntity.password)

            if (result is Resource.Failure) {
                val errorMessageResId = when (result.exception) {
                    is FirebaseAuthUserCollisionException -> R.string.error_email_already_registered
                    is FirebaseAuthInvalidUserException-> R.string.error_invalid_email
                    is FirebaseAuthWeakPasswordException -> R.string.error_weak_password
                    else -> R.string.error_unknown
                }

                throw AuthException.CustomAuthException(errorMessageResId)
            }

            // Verifica se o usuário foi cadastrado corretamente
            val currentUser = authRepository.currentUser
            if (currentUser != null) {
                emit(Resource.Success(true)) // Cadastro bem-sucedido
            } else {
                throw AuthException.UserNotFoundException() // Caso o usuário não tenha sido encontrado após o cadastro
            }
        } catch (e: Exception) {
            emit(Resource.Failure(e)) // Em caso de falha, emite o erro
        }
    }
}
