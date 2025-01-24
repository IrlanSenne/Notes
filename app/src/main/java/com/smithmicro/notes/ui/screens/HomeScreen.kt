package com.smithmicro.notes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smithmicro.notes.MainViewModel
import com.smithmicro.notes.R
import com.smithmicro.notes.Routes
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.ui.composables.NoteCard
import com.smithmicro.notes.ui.composables.NoteFloatingButton
import com.smithmicro.notes.ui.composables.NoteLoading
import com.smithmicro.notes.ui.composables.NoteTopBar
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
                navController.navigate(Routes.addWithNoteDetails("-1","",""))
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
                            content = note.content
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(196.dp))
            }
        }
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