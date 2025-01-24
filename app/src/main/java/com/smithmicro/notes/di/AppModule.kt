package com.smithmicro.notes.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smithmicro.notes.data.AuthRepository
import com.smithmicro.notes.data.AuthRepositoryImpl
import com.smithmicro.notes.data.localdatabase.NoteDao
import com.smithmicro.notes.data.localdatabase.NotesDatabase
import com.smithmicro.notes.data.repository.NotesRepository
import com.smithmicro.notes.data.repository.NotesRepositoryImpl
import com.smithmicro.notes.usecases.DeleteNoteUseCase
import com.smithmicro.notes.usecases.GetNoteByIdUseCase
import com.smithmicro.notes.usecases.GetNotesUseCase
import com.smithmicro.notes.usecases.LoginUseCase
import com.smithmicro.notes.usecases.LogoutUseCase
import com.smithmicro.notes.usecases.SaveNoteUseCase
import com.smithmicro.notes.usecases.SignupUseCase
import com.smithmicro.notes.usecases.UpdateNoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun providesAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideNotesDatabase(@ApplicationContext context: Context): NotesDatabase {
        return Room.databaseBuilder(
            context = context,
            NotesDatabase::class.java,
            "notes_database"
        ).build()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object DatabaseModule {
        @Provides
        @Singleton
        fun provideNoteDao(database: NotesDatabase): NoteDao {
            return database.noteDao()
        }
    }

    @Provides
    @Singleton
    fun provideNotesRepository(impl: NotesRepositoryImpl): NotesRepository = impl


    @Provides
    @Singleton
    fun provideGetNotesUseCase(repository: NotesRepository): GetNotesUseCase {
        return GetNotesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveNoteUseCase(
        repository: NotesRepository): SaveNoteUseCase {
        return SaveNoteUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteNoteUseCase(repository: NotesRepository): DeleteNoteUseCase {
        return DeleteNoteUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateNoteUseCase(repository: NotesRepository): UpdateNoteUseCase {
        return UpdateNoteUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetNoteByIdNoteUseCase(repository: NotesRepository): GetNoteByIdUseCase {
        return GetNoteByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository,
        notesRepository: NotesRepository,
        @ApplicationContext context: Context
    ): LoginUseCase {
        return LoginUseCase(authRepository, notesRepository, context)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        authRepository: AuthRepository,
        notesRepository: NotesRepository,
        @ApplicationContext context: Context
    ): LogoutUseCase {
        return LogoutUseCase(authRepository, notesRepository, context)
    }

    @Provides
    @Singleton
    fun provideSignupUseCase(
        authRepository: AuthRepository,
        @ApplicationContext context: Context
    ): SignupUseCase {
        return SignupUseCase(authRepository, context)
    }
}