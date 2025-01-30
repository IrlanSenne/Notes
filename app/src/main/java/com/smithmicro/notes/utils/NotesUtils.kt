package com.smithmicro.notes.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.tasks.Task
import com.smithmicro.notes.data.Resource
import com.smithmicro.notes.ui.components.NoteLoading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            if (it.exception != null) {
                cont.resumeWithException(it.exception!!)
            } else {
                cont.resume(it.result, null)
            }
        }
    }
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

fun hexToColor(hex: String): Color {
    val colorInt = android.graphics.Color.parseColor(hex)
    return Color(colorInt)
}

fun colorToHex(color: Color): String {
    val argb = color.toArgb()
    return String.format("#%08X", argb)
}

@Composable
fun handleResourceState(
    resource: Resource<Any>?,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onSuccess: (() -> Unit)? = null
) {
    when (resource) {
        is Resource.Failure -> {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = resource.exception.message.toString(),
                    actionLabel = "Close"
                )
            }
        }
        is Resource.Loading -> {
            NoteLoading()
        }
        is Resource.Success -> {
            onSuccess?.invoke()
        }
        else -> {}
    }
}