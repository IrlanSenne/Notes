package com.smithmicro.notes

import com.smithmicro.notes.core.MainViewModel
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.data.repository.AuthRepository
import com.smithmicro.notes.domain.usecases.DeleteNoteUseCase
import com.smithmicro.notes.domain.usecases.GetCredentialsUseCase
import com.smithmicro.notes.domain.usecases.GetNotesUseCase
import com.smithmicro.notes.domain.usecases.LoginUseCase
import com.smithmicro.notes.domain.usecases.LogoutUseCase
import com.smithmicro.notes.domain.usecases.SaveNoteUseCase
import com.smithmicro.notes.domain.usecases.SignupUseCase
import com.smithmicro.notes.domain.usecases.UpdateNoteUseCase
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @MockK
    private lateinit var authRepository: AuthRepository

    @MockK
    private lateinit var getNotesUseCase: GetNotesUseCase

    @MockK
    private lateinit var saveNoteUseCase: SaveNoteUseCase

    @MockK
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase

    @MockK
    private lateinit var updateNoteUseCase: UpdateNoteUseCase

    @MockK
    private lateinit var loginUseCase: LoginUseCase

    @MockK
    private lateinit var logoutUseCase: LogoutUseCase

    @MockK
    private lateinit var signupUseCase: SignupUseCase

    @MockK
    private lateinit var getCredentialsUseCase: GetCredentialsUseCase

    private lateinit var viewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        coEvery { getNotesUseCase.execute(Unit) } returns flowOf(listOf(
            NoteEntity(noteId = "1", title = "Note 1", content = "Content 1"),
            NoteEntity(noteId = "2", title = "Note 2", content = "Content 2")
        ))

        viewModel = MainViewModel(
            authRepository = authRepository,
            getNotesUseCase = getNotesUseCase,
            saveNoteUseCase = saveNoteUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            updateNoteUseCase = updateNoteUseCase,
            loginUseCase = loginUseCase,
            logoutUseCase = logoutUseCase,
            signupUseCase = signupUseCase,
            getCredentialsUseCase = getCredentialsUseCase
        )
    }

    @Test
    fun `addNoteUpdatesTheFlowWhenNote_IsAddedSuccessfully`() = runTest {
        val mockNote = NoteEntity(noteId = "1", title = "Note", content = "Content")

        coEvery { saveNoteUseCase.execute(mockNote) } just Runs

        viewModel.addNote(mockNote)

        advanceUntilIdle()

        coVerify { saveNoteUseCase.execute(mockNote) }
    }

    @Test
    fun `fetchNotesUpdatesTheNotesFlow_WhenNotesAreFetchedSuccessfully`() = runTest {
        val mockNotes = listOf(
            NoteEntity(noteId = "1", title = "Note 1", content = "Content 1"),
            NoteEntity(noteId = "2", title = "Note 2", content = "Content 2")
        )

        coEvery { getNotesUseCase.execute(Unit) } returns flowOf(mockNotes)

        viewModel.fetchNotes()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.notesFlow.value is Resource.Success)
        assertEquals(mockNotes, (viewModel.notesFlow.value as Resource.Success).result)
    }

    @Test
    fun `getCredentialsUpdatesCredentialsFlow_OnSuccess`() = runTest {
        val mockCredentials = Pair("test@example.com", "password123")
        coEvery { getCredentialsUseCase.execute(Unit) } returns mockCredentials

        viewModel.getSavedCredentials()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockCredentials, viewModel.credentialsFlow.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}

