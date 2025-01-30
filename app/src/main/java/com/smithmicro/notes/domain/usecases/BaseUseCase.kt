package com.smithmicro.notes.domain.usecases

interface BaseUseCase<in Input, out Output> {
    suspend fun execute(input: Input): Output
}