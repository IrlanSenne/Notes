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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smithmicro.notes.core.MainViewModel
import com.smithmicro.notes.R
import com.smithmicro.notes.core.Routes.Companion.NEW_NOTE
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.data.entities.NoteEntity
import com.smithmicro.notes.ui.components.NoteColorPicker
import com.smithmicro.notes.ui.components.NoteTopBar
import com.smithmicro.notes.ui.components.NotesOutlinedTextField
import com.smithmicro.notes.ui.theme.SmithMicroNotesTheme
import com.smithmicro.notes.utils.colorToHex
import com.smithmicro.notes.utils.handleResourceState
import com.smithmicro.notes.utils.hexToColor
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.random.Random

@Composable
fun NoteAddUpdateScreen(
    viewModel: MainViewModel? = null,
    navController: NavController?,
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
                    navController?.navigateUp()
                },
                extraIconClick = {
                    isErrorTitle = title.isEmpty()
                    isErrorContent = content.isEmpty()

                    if (isErrorTitle || isErrorContent) return@NoteTopBar

                    val noteEntity = NoteEntity(
                        noteId = noteId.takeIf { it != NEW_NOTE } ?: Random.nextInt(1, Int.MAX_VALUE).toString(),
                        title = title,
                        content = content,
                        color = colorToHex(selectedColor)
                    )

                    if (noteId != NEW_NOTE) {
                        viewModel?.updateNote(noteEntity)
                    } else {
                        viewModel?.addNote(noteEntity)
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
            NotesOutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (it.isNotEmpty()) isErrorTitle = false
                },
                label = stringResource(R.string.title),
                isError = isErrorTitle,
                textStyle = TextStyle(fontSize = 24.sp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            NotesOutlinedTextField(
                value = content,
                onValueChange = {
                    content = it
                    if (it.isNotEmpty()) isErrorContent = false
                },
                label = stringResource(R.string.content),
                isError = isErrorContent,
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 10,
                height = 200.dp
            )

            Spacer(modifier = Modifier.height(24.dp))
            NoteColorPicker { selectedColor = it }

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

    handleResourceState(
        resource = addNoteFlow,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        onSuccess = {
            viewModel?.resetState()
            navController?.popBackStack()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NoteAddUpdateScreen() {
    SmithMicroNotesTheme {
        NoteAddUpdateScreen(null, null, "12345", "SmithMicro", "Content Description test")
    }
}


