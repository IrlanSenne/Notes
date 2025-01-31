package com.smithmicro.notes

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.data.localdatabase.NoteDao
import com.smithmicro.notes.data.repository.NotesRepository
import com.smithmicro.notes.data.repository.NotesRepositoryImpl
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class NotesRepositoryTest {

    @MockK
    private lateinit var noteDao: NoteDao

    @MockK
    private lateinit var firebaseFirestore: FirebaseFirestore

    private lateinit var notesRepository: NotesRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        notesRepository = NotesRepositoryImpl(noteDao, firebaseFirestore)
    }

    @Test
    fun saveNoteShouldInsertNoteWhenItDoesNotExist() = runTest {
        val note = NoteEntity("1", "Title", "Content", "#FFFFFF", System.currentTimeMillis())

        coEvery { noteDao.getNoteById(note.noteId) } returns null
        coEvery { noteDao.insertNote(note) } just Runs

        notesRepository.saveNote(note)

        coVerify { noteDao.insertNote(note) }
    }

    @Test
    fun saveNoteShouldUpdateNoteWhenItAlreadyExists() = runTest {
        val note = NoteEntity("1", "Updated Title", "Updated Content")

        coEvery { noteDao.getNoteById(note.noteId) } returns note
        coEvery { noteDao.updateNote(note) } just Runs

        notesRepository.saveNote(note)

        coVerify { noteDao.updateNote(note) }
    }

    @Test
    fun deleteNoteShouldCallDaoDeleteMethod() = runTest {
        val note = NoteEntity("1", "Title", "Content")

        coEvery { noteDao.deleteNote(note) } just Runs

        notesRepository.deleteNote(note)

        coVerify { noteDao.deleteNote(note) }
    }

    @Test
    fun getNotesLocalShouldReturnFlowOfNotes() = runTest {
        val notesList = listOf(
            NoteEntity("1", "Note 1", "Content 1"),
            NoteEntity("2", "Note 2", "Content 2")
        )

        coEvery { noteDao.getAllNotes() } returns flowOf(notesList)

        val result = notesRepository.getNotesLocal().first()

        assertEquals(notesList, result)
    }

    @Test
    fun deleteAllNotesLocalShouldCallDaoDeleteAllMethod() = runTest {
        coEvery { noteDao.deleteAllNotes() } just Runs

        notesRepository.deleteAllNotesLocal()

        coVerify { noteDao.deleteAllNotes() }
    }

    @Test
    fun getNotesRemoteShouldReturnFlowOfNotesFromFirestore() = runTest {
        val userId = "testUser"
        val notesList = listOf(
            NoteEntity("1", "Note 1", "Content 1"),
            NoteEntity("2", "Note 2", "Content 2")
        )

        val snapshot: QuerySnapshot = mockk()
        val document1: DocumentSnapshot = mockk()
        val document2: DocumentSnapshot = mockk()

        every { document1.toObject(NoteEntity::class.java) } returns notesList[0]
        every { document2.toObject(NoteEntity::class.java) } returns notesList[1]
        every { snapshot.documents } returns listOf(document1, document2)

        val collectionRef: CollectionReference = mockk()
        every { firebaseFirestore.collection(userId) } returns collectionRef

        val listenerRegistration: ListenerRegistration = mockk(relaxed = true)

        val slot = mutableListOf<com.google.firebase.firestore.EventListener<QuerySnapshot>>()

        every { collectionRef.addSnapshotListener(capture(slot)) } answers {
            slot.captured().onEvent(snapshot, null)
            listenerRegistration
        }

        val result = notesRepository.getNotesRemote(userId).first()

        assertEquals(notesList, result)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}