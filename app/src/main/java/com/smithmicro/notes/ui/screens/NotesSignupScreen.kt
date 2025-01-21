package com.smithmicro.notes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.smithmicro.notes.MainViewModel
import com.smithmicro.notes.R
import com.smithmicro.notes.Routes
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.ui.composables.NotesTextField
import kotlinx.coroutines.launch

@Composable
fun NotesSignupScreen(
    viewModel: MainViewModel?,
    navController: NavController
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authResource = viewModel?.signupFlow?.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            NotesTextField(username, { username = it }, stringResource(id = R.string.username))

            Spacer(modifier = Modifier.height(16.dp))
            NotesTextField(email, { email = it }, stringResource(id = R.string.email))

            Spacer(modifier = Modifier.height(16.dp))
            NotesTextField(password, { password = it }, stringResource(id = R.string.password), true)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel?.signupUser(username, email, password)
                }
            ) {
                Text(
                    text = stringResource(id = R.string.signup),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.clickable {
                    navController.navigateUp()
                },
                text = stringResource(id = R.string.already_have_account),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            authResource?.value?.let {
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
                        CircularProgressIndicator()
                    }

                    is Resource.Success -> {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SIGNUP) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
