package com.smithmicro.notes.usecases

import android.content.Context
import android.util.Log
import com.smithmicro.notes.data.AuthRepository
import com.smithmicro.notes.data.entities.UserEntity
import com.smithmicro.notes.utils.isInternetAvailable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SignupUseCase(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : BaseUseCase<UserEntity, Flow<Boolean>> {

    override suspend fun execute(userEntity: UserEntity): Flow<Boolean> = flow {
        try {
            if (isInternetAvailable(context)) {
                userEntity.name?.let { authRepository.signup(it, userEntity.email, userEntity.password) }

                val currentUser = authRepository.currentUser
                if (currentUser != null) {
                    emit(true)
                } else {
                    emit(false)
                }
            } else {
                throw Exception("No internet connection")
            }
        } catch (e: Exception) {
            emit(false)
        }
    }
}