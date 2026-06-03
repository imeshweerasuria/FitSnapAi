package com.adheesha.fitsnapai.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun getCurrentUser() = firebaseAuth.currentUser

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun registerUser(email: String, password: String) {
        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()
    }

    suspend fun loginUser(email: String, password: String) {
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()
    }

    fun logoutUser() {
        firebaseAuth.signOut()
    }
}