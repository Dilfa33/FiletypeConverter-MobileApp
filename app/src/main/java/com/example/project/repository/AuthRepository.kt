package com.example.project.repository

interface AuthRepository {
    suspend fun register(email: String, password: String)
    suspend fun login(email: String, password: String)
    suspend fun signInWithGoogle(idToken: String)
    suspend fun sendPasswordResetEmail(email: String)
    fun logout()
    fun getCurrentUserId(): String?
    fun isLoggedIn(): Boolean
    fun getPhotoUrl(): String?
    fun getDisplayName(): String?
}
