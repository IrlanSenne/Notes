package com.smithmicro.notes

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smithmicro.notes.Routes.Companion.NEW_NOTE
import com.smithmicro.notes.ui.screens.HomeScreen
import com.smithmicro.notes.ui.screens.NoteAddScreen
import com.smithmicro.notes.ui.screens.NotesLoginScreen
import com.smithmicro.notes.ui.screens.NotesSignupScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MainNavigation(mainViewModel: MainViewModel) {
    val navController = rememberNavController()

    val currentUser = mainViewModel.getCurrentUser()
    val startDestination = if (currentUser != null) Routes.HOME else Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Routes.LOGIN,
            enterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            NotesLoginScreen(
                viewModel = mainViewModel,
                navController = navController
            )
        }

        composable(
            route = Routes.SIGNUP,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }
        ) {
            NotesSignupScreen(
                viewModel = mainViewModel,
                navController = navController
            )
        }

        composable(
            route = Routes.HOME,
            enterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            HomeScreen(
                viewModel = mainViewModel,
                navController = navController
            )
        }

        composable(
            route = "${Routes.ADD}?noteId={noteId}&title={title}&content={content}&color={color}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.StringType
                    defaultValue = NEW_NOTE
                },
                navArgument("title") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("content") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("color") {
                    type = NavType.StringType
                    defaultValue = "#FFFFFFFF"
                }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: NEW_NOTE
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val color = backStackEntry.arguments?.getString("color") ?: "#FFFFFFFF"
            val content = backStackEntry.arguments?.getString("content")?.let {
                URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
            } ?: ""

            NoteAddScreen(
                viewModel = mainViewModel,
                navController = navController,
                noteId = noteId,
                noteTitle = title,
                noteContent = content,
                noteColor = color
            )
        }
    }
}

class Routes {
    companion object {
        const val LOGIN = "login"
        const val SIGNUP = "signup"
        const val HOME = "home"
        const val ADD = "add"
        const val NEW_NOTE = "-1"

        fun addWithNoteDetails(noteId: String?, title: String?, content: String?, color: String?) =
            "$ADD?noteId=$noteId&title=$title&content=${URLEncoder.encode(content, StandardCharsets.UTF_8.toString())}&color=${URLEncoder.encode(color, StandardCharsets.UTF_8.toString())}"
    }
}
