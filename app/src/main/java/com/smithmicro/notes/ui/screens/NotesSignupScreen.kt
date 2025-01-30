package com.smithmicro.notes.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smithmicro.notes.core.MainViewModel
import com.smithmicro.notes.R
import com.smithmicro.notes.core.Routes
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.ui.components.NoteLoading
import com.smithmicro.notes.ui.components.NoteTopBar
import com.smithmicro.notes.ui.components.NotesTextField
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

    var hasAttemptedSignup by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            NoteTopBar(
                colorIcon = Color.White,
                colorBackground = MaterialTheme.colorScheme.primary,
                navigationIconClick = {
                    navController.navigateUp()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_signup),
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = "-",
                    modifier = Modifier
                        .size(130.dp)
                        .padding(bottom = 16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 62.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            focusManager.clearFocus()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        text = stringResource(R.string.create_new_account),
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(36.dp))
                    NotesTextField(username, { username = it }, stringResource(id = R.string.username))

                    Spacer(modifier = Modifier.height(18.dp))
                    NotesTextField(email, { email = it }, stringResource(id = R.string.email))

                    Spacer(modifier = Modifier.height(18.dp))
                    NotesTextField(password, { password = it }, stringResource(id = R.string.password), true)

                    Spacer(modifier = Modifier.height(26.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            hasAttemptedSignup = true
                            viewModel?.signupUser(username, email, password)
                            focusManager.clearFocus()
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
                }
            }
        }

        authResource?.value?.let {
            when (it) {
                is Resource.Failure -> {
                    if (hasAttemptedSignup) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = it.exception.message.toString(),
                                actionLabel = "Close"
                            )
                        }
                        hasAttemptedSignup = false
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