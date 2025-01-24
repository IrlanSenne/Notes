package com.smithmicro.notes.usecases

interface BaseUseCase<in Input, out Output> {
    suspend fun execute(input: Input): Output
}