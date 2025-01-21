package com.smithmicro.notes

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smithmicro.notes.ui.screens.HomeScreen
import com.smithmicro.notes.ui.screens.NotesLoginScreen
import com.smithmicro.notes.ui.screens.NotesSignupScreen

@Composable
fun MainNavigation(mainViewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
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
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
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
    }
}


class Routes {
    companion object {
        const val LOGIN = "login"
        const val SIGNUP = "signup"
        const val HOME = "home"
    }
}
