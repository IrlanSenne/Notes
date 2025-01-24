package com.smithmicro.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.smithmicro.notes.data.AuthRepository
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.data.entities.UserEntity
import com.smithmicro.notes.usecases.DeleteNoteUseCase
import com.smithmicro.notes.usecases.GetNotesUseCase
import com.smithmicro.notes.usecases.LoginUseCase
import com.smithmicro.notes.usecases.LogoutUseCase
import com.smithmicro.notes.usecases.SaveNoteUseCase
import com.smithmicro.notes.usecases.SignupUseCase
import com.smithmicro.notes.usecases.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getNotesUseCase: GetNotesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val signupUseCase: SignupUseCase
) : ViewModel() {
    private val _loginFlow = MutableStateFlow<Resource<List<NoteEntity>>?>(null)
    val loginFlow: StateFlow<Resource<List<NoteEntity>>?> = _loginFlow

    private val _logoutFlow = MutableStateFlow<Resource<Boolean>?>(null)
    val logoutFlow: StateFlow<Resource<Boolean>?> = _logoutFlow

    private val _signupFlow = MutableStateFlow<Resource<Boolean>?>(null)
    val signupFlow: StateFlow<Resource<Boolean>?> = _signupFlow

    private val _notesFlow = MutableStateFlow<Resource<List<NoteEntity>>?>(null)
    val notesFlow: StateFlow<Resource<List<NoteEntity>>?> = _notesFlow.asStateFlow()

    private val _noteFlow = MutableStateFlow<Resource<NoteEntity>?>(null)
    val noteFlow: StateFlow<Resource<NoteEntity>?> = _noteFlow.asStateFlow()

    private val _addNoteFlow = MutableStateFlow<Resource<Boolean>?>(null)
    val addNoteFlow: StateFlow<Resource<Boolean>?> = _addNoteFlow.asStateFlow()

    fun addNote(note: NoteEntity) {
        _addNoteFlow.value = Resource.Loading
        viewModelScope.launch {
            try {
                saveNoteUseCase.execute(note)
                fetchNotes()
                _addNoteFlow.value = Resource.Success(true)
            } catch (e: Exception) {
                _addNoteFlow.value = Resource.Failure(e)
            }
        }
    }

    fun fetchNotes() {
        viewModelScope.launch {
            _notesFlow.value = Resource.Loading
            getNotesUseCase.execute(Unit)
                .collect { notes ->
                    _notesFlow.value =  Resource.Success(notes)
                }
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            _addNoteFlow.value = Resource.Loading
            try {
                updateNoteUseCase.execute(note)
                fetchNotes()
                _addNoteFlow.value = Resource.Success(true)
            } catch (e: Exception) {
                _noteFlow.value = Resource.Failure(e)
            }
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            _addNoteFlow.value = Resource.Loading
            try {
                deleteNoteUseCase.execute(note)
                fetchNotes()
                _addNoteFlow.value = Resource.Success(true)
            } catch (e: Exception) {
                _addNoteFlow.value = Resource.Failure(e)
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginFlow.value = Resource.Loading

            loginUseCase.execute(UserEntity(email = email, password = password))
                .collect { result ->
                    _logoutFlow.value = null
                    _loginFlow.value = result
                }
        }
    }

    fun signupUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            _signupFlow.value = Resource.Loading
            try {
                signupUseCase.execute(UserEntity(name, email, password))
                    .collect { isSuccess ->
                        if (isSuccess) {
                            _logoutFlow.value = null
                            _signupFlow.value = Resource.Success(true)
                        } else {
                            _signupFlow.value = Resource.Failure(Exception("Signup failed"))
                        }
                    }
            } catch (e: Exception) {
                _signupFlow.value = Resource.Failure(e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutFlow.value = Resource.Loading
            logoutUseCase.execute(Unit)
                .collect { isSuccess ->
                    if (isSuccess) {
                        _logoutFlow.value = Resource.Success(true)
                        _loginFlow.value = null
                    } else {
                        _logoutFlow.value = Resource.Failure(Exception("Logout failed"))
                    }
                }
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return authRepository.currentUser
    }

    fun resetState() {
        viewModelScope.launch {
            _signupFlow.value = null
            _loginFlow.value = null
            _addNoteFlow.value = null
            _noteFlow.value = null
            _logoutFlow.value = null
        }
    }
}