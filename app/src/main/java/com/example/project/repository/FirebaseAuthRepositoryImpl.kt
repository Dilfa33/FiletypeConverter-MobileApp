package com.example.project.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun register(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid

    override fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun getPhotoUrl(): String? = firebaseAuth.currentUser?.photoUrl?.toString()

    override fun getDisplayName(): String? = firebaseAuth.currentUser?.displayName
}
