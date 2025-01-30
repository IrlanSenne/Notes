package com.smithmicro.notes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smithmicro.notes.core.MainViewModel
import com.smithmicro.notes.R
import com.smithmicro.notes.core.Routes.Companion.NEW_NOTE
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.ui.components.NoteColorPicker
import com.smithmicro.notes.ui.components.NoteLoading
import com.smithmicro.notes.ui.components.NoteTopBar
import com.smithmicro.notes.utils.colorToHex
import com.smithmicro.notes.utils.hexToColor
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.random.Random

@Composable
fun NoteAddScreen(
    viewModel: MainViewModel? = null,
    navController: NavController,
    noteId: String? = null,
    noteTitle: String? = null,
    noteContent: String? = null,
    noteColor: String? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val addNoteFlow = viewModel?.addNoteFlow?.collectAsState()?.value
    val noteFlow = viewModel?.noteFlow?.collectAsState()?.value
    val note = (noteFlow as? Resource.Success)?.result

    var title by remember(note) { mutableStateOf(noteTitle ?: "") }
    var content by remember(note) { mutableStateOf(URLDecoder.decode(noteContent, StandardCharsets.UTF_8.toString()) ?: "") }
    var isErrorTitle by remember { mutableStateOf(false) }
    var isErrorContent by remember { mutableStateOf(false) }

    var selectedColor by remember { mutableStateOf(hexToColor(noteColor ?: "#FFFFFFFF")) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            NoteTopBar(
                title = stringResource(R.string.my_notes),
                colorBackground = selectedColor,
                navigationIconClick = {
                    viewModel?.resetState()
                    navController.navigateUp()
                },
                extraIconClick = {
                    if (title.isEmpty()) {
                        isErrorTitle = true
                    }
                    if (content.isEmpty()) {
                        isErrorContent = true
                    }

                    if (title.isNotEmpty() && content.isNotEmpty()) {
                        isErrorTitle = false
                        isErrorContent = false
                        if (noteId != NEW_NOTE) {
                            viewModel?.updateNote(
                                NoteEntity(
                                    noteId = noteId ?: "",
                                    title = title,
                                    content = content,
                                    color = colorToHex(selectedColor)
                                )
                            )
                        } else {
                            val randomId = Random.nextInt(1, Int.MAX_VALUE).toString()
                            val noteEntity = NoteEntity(
                                noteId = randomId,
                                title = title,
                                content = content,
                                color = colorToHex(selectedColor)
                            )

                            viewModel?.addNote(noteEntity)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(selectedColor)
                .padding(horizontal = 16.dp)
                .padding(top = paddingValues.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotEmpty()) {
                        isErrorTitle = false
                    }
                },
                label = {
                    Text(
                        stringResource(R.string.title),
                        style = TextStyle(fontSize = 24.sp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleLarge,
                isError = isErrorTitle
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                    if (it.isNotEmpty()) {
                        isErrorContent = false
                    }
                },
                label = {
                    Text(
                        stringResource(R.string.content),
                        style = TextStyle(fontSize = 18.sp),
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10,
                textStyle = MaterialTheme.typography.bodyLarge,
                isError = isErrorContent
            )

            Spacer(modifier = Modifier.height(24.dp))
            NoteColorPicker {
                selectedColor = it
            }

            if (noteId != NEW_NOTE) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    onClick = {
                        noteId?.let {
                            viewModel?.deleteNote(
                                NoteEntity(noteId, title, content)
                            )
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    addNoteFlow?.let {
        when (it) {
            is Resource.Failure -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = it.exception.message.toString(),
                        actionLabel = "Close"
                    )
                }
            }

            is Resource.Loading -> NoteLoading()

            is Resource.Success -> {
                viewModel.resetState()
                navController.popBackStack()
            }
        }
    }
}


