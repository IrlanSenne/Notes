package com.smithmicro.notes

import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.data.repository.AuthRepository
import com.smithmicro.notes.data.repository.NotesRepository
import com.smithmicro.notes.domain.usecases.GetCredentialsUseCase
import com.smithmicro.notes.domain.usecases.GetNotesUseCase
import com.smithmicro.notes.domain.usecases.SaveNoteUseCase
import com.smithmicro.notes.domain.usecases.UpdateNoteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class UseCaseTest {

    @Mock
    private lateinit var notesRepository: NotesRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var getNotesUseCase: GetNotesUseCase
    private lateinit var saveNoteUseCase: SaveNoteUseCase
    private lateinit var getCredentialsUseCase: GetCredentialsUseCase
    private lateinit var updateNoteUseCase: UpdateNoteUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getNotesUseCase = GetNotesUseCase(notesRepository)
        saveNoteUseCase = SaveNoteUseCase(notesRepository)
        getCredentialsUseCase = GetCredentialsUseCase(authRepository)
        updateNoteUseCase = UpdateNoteUseCase(notesRepository)
    }

    @Test
    fun getNotesAreFetchedSuccessfully_shouldReturnNotes() = runBlocking {
        val mockNotes = listOf(
            NoteEntity(noteId = "1", title = "Note 1", content = "Content 1"),
            NoteEntity(noteId = "2", title = "Note 2", content = "Content 2")
        )

        Mockito.`when`(notesRepository.getNotesLocal()).thenReturn(flowOf(mockNotes))

        val result = getNotesUseCase.execute(Unit).first()

        assertEquals(mockNotes, result)
    }

    @Test
    fun getNotesErrorOccurs_shouldReturnEmptyList() = runBlocking {
        whenever(notesRepository.getNotesLocal()).thenReturn(flow {
            throw RuntimeException("Error fetching notes")
        })

        val result = getNotesUseCase.execute(Unit).first()

        assertEquals(emptyList<NoteEntity>(), result)
    }


    @Test
    fun saveNoteExecutesSuccessfully() = runBlocking {
        val note = NoteEntity(noteId = "1", title = "Note 1", content = "Content of note 1")

        saveNoteUseCase.execute(note)

        verify(notesRepository).saveNote(note)
    }

    @Test
    fun `saveNoteThrowsException_WhenRepositoryFails`() = runBlocking {
        val note = NoteEntity(noteId = "1", title = "Note 1", content = "Content of note 1")

        whenever(notesRepository.saveNote(any())).thenThrow(RuntimeException("Error saving note"))

        try {
            saveNoteUseCase.execute(note)
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals("Error saving note", e.message)
        }
    }

    @Test
    fun `getCredentialsAreFetchedSuccessfully_shouldReturnCredentials`() = runBlocking {
        val mockCredentials = Pair("test@example.com", "password123")

        whenever(authRepository.getCredentials()).thenReturn(mockCredentials)

        val result = getCredentialsUseCase.execute(Unit)

        assertEquals(mockCredentials, result)
    }

    @Test
    fun `getErrorOccursWhileFetchingCredentials_shouldReturnNullPair`() = runBlocking {
        whenever(authRepository.getCredentials()).thenThrow(RuntimeException("Error fetching credentials"))

        val result = getCredentialsUseCase.execute(Unit)

        assertEquals(Pair(null, null), result)
    }

    @Test
    fun `updateNoteIsUpdatedSuccessfully_shouldCallUpdateNoteLocal`() = runBlocking {
        val noteToUpdate =
            NoteEntity(noteId = "1", title = "Updated Note", content = "Updated Content")

        updateNoteUseCase.execute(noteToUpdate)

        verify(notesRepository).updateNoteLocal(noteToUpdate)
    }

    @Test
    fun `updateErrorOccursWhileUpdatingNote_shouldThrowException`() = runBlocking {
        val noteToUpdate =
            NoteEntity(noteId = "1", title = "Updated Note", content = "Updated Content")

        whenever(notesRepository.updateNoteLocal(any())).thenThrow(RuntimeException("Error updating note"))

        try {
            updateNoteUseCase.execute(noteToUpdate)
            fail("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals("Error updating note", e.message)
        }
    }
}