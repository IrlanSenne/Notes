package com.smithmicro.notes.domain.usecases

import com.smithmicro.notes.data.repository.AuthRepository

class GetCredentialsUseCase(
    private val authRepository: AuthRepository
) : BaseUseCase<Unit, Pair<String?, String?>> {

    override suspend fun execute(input: Unit): Pair<String?, String?> {
        return try {
            authRepository.getCredentials()
        } catch (e: Exception) {
            Pair(null, null)
        }
    }
}