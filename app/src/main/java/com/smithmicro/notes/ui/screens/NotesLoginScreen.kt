package com.smithmicro.notes.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smithmicro.notes.MainViewModel
import com.smithmicro.notes.R
import com.smithmicro.notes.Routes
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.ui.composables.NoteLoading
import com.smithmicro.notes.ui.composables.NotesTextField
import kotlinx.coroutines.launch

@Composable
fun NotesLoginScreen(viewModel: MainViewModel?, navController: NavController) {
    var email by remember { mutableStateOf("irlan@gmail.com") }
    var password by remember { mutableStateOf("123456789") }

    val loginFlow = viewModel?.loginFlow?.collectAsState()

    var hasAttemptedLogin by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val focusManager = LocalFocusManager.current

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    focusManager.clearFocus()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_login),
                contentDescription = "-",
                modifier = Modifier
                    .size(148.dp)
                    .padding(bottom = 16.dp)
            )
            NotesTextField(email, { email = it }, stringResource(R.string.email))

            Spacer(modifier = Modifier.height(16.dp))
            NotesTextField(
                password,
                { password = it },
                stringResource(R.string.password),
                true
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    hasAttemptedLogin = true
                    viewModel?.loginUser(email, password)
                    focusManager.clearFocus()
                },
            ) {
                Text(
                    text = stringResource(R.string.login),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier
                    .clickable {
                        navController.navigate(Routes.SIGNUP)
                    },
                text = stringResource(R.string.register),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        loginFlow?.value?.let {
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

                is Resource.Success -> {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            }
        }
    }
}