package com.smithmicro.notes.data.exception

import com.smithmicro.notes.R

sealed class AuthException(val messageResId: Int) : Exception() {
    class InvalidEmailException : AuthException(R.string.error_invalid_email)
    class InvalidPasswordException : AuthException(R.string.error_invalid_password)
    class UserNotFoundException : AuthException(R.string.error_user_not_found)
    class IncorrectCredentialsException : AuthException(R.string.error_incorrect_credentials)
    class NoInternetConnectionException : AuthException(R.string.error_no_internet_connection)
    class CustomAuthException(messageResId: Int) : AuthException(messageResId)
}
