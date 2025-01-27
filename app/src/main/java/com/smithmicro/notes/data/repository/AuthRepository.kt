package com.smithmicro.notes.data.repository

import com.google.firebase.auth.FirebaseUser
import com.smithmicro.notes.data.Resource

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    suspend fun signup(name: String, email: String, password: String): Resource<FirebaseUser>
    fun logout()
    fun saveCredentials(email: String, password: String)
    fun getCredentials(): Pair<String?, String?>
}