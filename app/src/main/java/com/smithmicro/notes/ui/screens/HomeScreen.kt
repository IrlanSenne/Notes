package com.smithmicro.notes.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smithmicro.notes.MainViewModel
import com.smithmicro.notes.R
import com.smithmicro.notes.Routes
import com.smithmicro.notes.Routes.Companion.NEW_NOTE
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.ui.composables.NoteCard
import com.smithmicro.notes.ui.composables.NoteFloatingButton
import com.smithmicro.notes.ui.composables.NoteLoading
import com.smithmicro.notes.ui.composables.NoteTopBar
import com.smithmicro.notes.ui.composables.NotesEmptyWarningBox
import com.smithmicro.notes.utils.hexToColor
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: MainViewModel?,
    navController: NavController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val notesFlow = viewModel?.notesFlow?.collectAsState()?.value
    val logoutFlow = viewModel?.logoutFlow?.collectAsState()?.value
    val notes = (notesFlow as? Resource.Success)?.result ?: emptyList()

    LaunchedEffect(true) { viewModel?.fetchNotes() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            NoteTopBar(
                title = stringResource(R.string.notes),
                extraIconResId = R.drawable.ic_logout,
                extraIconClick = {
                    viewModel?.logout()
                }
            )
        },
        floatingActionButton = {
            NoteFloatingButton() {
                navController.navigate(Routes.addWithNoteDetails(NEW_NOTE,"","", "#FFFFFFFF"))
            }
        }
    ) { paddingValues ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 106.dp),
            columns = StaggeredGridCells.Adaptive(200.dp),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            itemsIndexed(notes) { index, note ->
                NoteCard(note = note) {
                    navController.navigate(
                        Routes.addWithNoteDetails(
                            noteId = note.noteId,
                            title = note.title,
                            content = note.content,
                            color = note.color
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(196.dp))
            }
        }

        NotesEmptyWarningBox(notes.isEmpty())
    }

    notesFlow?.let {
        when (it) {
            is Resource.Failure -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = it.exception.message.toString(),
                        actionLabel = "Close"
                    )
                }
            }

            is Resource.Loading -> {
                NoteLoading()
            }

            else -> {}
        }
    }

    logoutFlow?.let {
        when (it) {
            is Resource.Loading -> {
                NoteLoading()
            }

            is Resource.Success -> {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }

            is Resource.Failure -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = it.exception.message.toString(),
                        actionLabel = "Close"
                    )
                }
            }
        }
    }
}