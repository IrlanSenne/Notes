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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smithmicro.notes.core.MainViewModel
import com.smithmicro.notes.R
import com.smithmicro.notes.core.Routes
import com.smithmicro.notes.ui.components.NoteTopBar
import com.smithmicro.notes.ui.components.NotesTextField
import com.smithmicro.notes.ui.theme.SmithMicroNotesTheme
import com.smithmicro.notes.utils.handleResourceState

enum class AuthType { LOGIN, SIGNUP }

@Composable
fun NoteAuthScreen(
    viewModel: MainViewModel?,
    navController: NavController?,
    authType: AuthType
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hasAttemptedAuth by remember { mutableStateOf(false) }

    val savedCredentials = viewModel?.credentialsFlow?.collectAsState(initial = Pair("", ""))?.value

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    val authFlow = when (authType) {
        AuthType.LOGIN -> viewModel?.loginFlow?.collectAsState()
        AuthType.SIGNUP -> viewModel?.signupFlow?.collectAsState()
    }

    if (authType == AuthType.LOGIN) {
        email = savedCredentials?.first ?: ""
        password = savedCredentials?.second ?: ""

        LaunchedEffect(Unit) { viewModel?.getSavedCredentials() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (authType == AuthType.SIGNUP) {
                NoteTopBar(
                    colorIcon = Color.White,
                    colorBackground = MaterialTheme.colorScheme.primary,
                    navigationIconClick = { navController?.navigateUp() }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = if (authType == AuthType.LOGIN) R.drawable.ic_login_person else R.drawable.ic_signup),
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = "-",
                    modifier = Modifier
                        .size(if (authType == AuthType.LOGIN) 148.dp else 130.dp)
                        .padding(bottom = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.65f)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topStart = 62.dp, topEnd = 62.dp)
                    )
                    .padding(horizontal = 16.dp)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) { focusManager.clearFocus() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = stringResource(if (authType == AuthType.LOGIN) R.string.login else R.string.create_new_account),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = if (authType == AuthType.LOGIN) 40.sp else 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))
                if (authType == AuthType.LOGIN) {
                    Text(
                        text = stringResource(R.string.login_to_continue),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                if (authType == AuthType.SIGNUP) {
                    NotesTextField(username, { username = it }, stringResource(R.string.username))
                    Spacer(modifier = Modifier.height(18.dp))
                }
                NotesTextField(email, { email = it }, stringResource(R.string.email))
                Spacer(modifier = Modifier.height(18.dp))
                NotesTextField(password, { password = it }, stringResource(R.string.password), true)

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        hasAttemptedAuth = true
                        if (authType == AuthType.LOGIN) {
                            viewModel?.loginUser(email, password)
                        } else {
                            viewModel?.signupUser(username, email, password)
                        }
                        focusManager.clearFocus()
                    }
                ) {
                    Text(
                        text = stringResource(if (authType == AuthType.LOGIN) R.string.login_in else R.string.signup),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.clickable {
                        if (authType == AuthType.LOGIN) {
                            navController?.navigate(Routes.SIGNUP)
                        } else {
                            navController?.navigateUp()
                        }
                    },
                    text = stringResource(if (authType == AuthType.LOGIN) R.string.register else R.string.already_have_account),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

        }

        handleResourceState(
            hasAttempted = hasAttemptedAuth,
            hasAttemptedChange = { hasAttemptedAuth = false },
            resource = authFlow?.value,
            snackBarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
            onSuccess = {
                navController?.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteLOGINAuthScreenPreview() {
    SmithMicroNotesTheme {
        NoteAuthScreen(null, null, AuthType.LOGIN)
    }
}

@Preview(showBackground = true)
@Composable
fun NoteSIGNUPAuthScreenPreview() {
    SmithMicroNotesTheme {
        NoteAuthScreen(null, null, AuthType.SIGNUP)
    }
}
