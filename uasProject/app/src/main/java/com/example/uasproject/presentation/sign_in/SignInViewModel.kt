package com.example.uasproject.presentation.sign_in

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.uasproject.data.model.SignInResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult){
        _state.update {
            it.copy(
                isSignInSuccessfull = result.data != null,
                signInError = result.errorMessage
            )
        }

        // Simpan user ke Firestore jika sign in berhasil
        if (result.data != null) {
            saveUserToFirestore(result.data)
        }
    }

    /**
     * Fungsi untuk menyimpan user ke Firestore
     * Sesuai dengan model UserData yang ada: userId, username, profilePictureUrl
     */
    private fun saveUserToFirestore(userData: com.example.uasproject.data.model.UserData) {
        // userId sudah ada di userData dari Google Sign In
        val userId = userData.userId

        // Ambil email dari Firebase Auth (karena tidak ada di UserData model)
        val email = auth.currentUser?.email ?: ""

        // Data yang akan disimpan ke Firestore
        val userDocument = hashMapOf(
            "userId" to userId,
            "email" to email,  // Dari Firebase Auth
            "username" to (userData.username ?: ""),
            "profilePictureUrl" to (userData.profilePictureUrl ?: ""),
            "createdAt" to com.google.firebase.Timestamp.now(),
            "lastLogin" to com.google.firebase.Timestamp.now()
        )

        // Simpan ke Firestore: users/{userId}
        db.collection("users")
            .document(userId)  // 👈 userId masuk ke database di sini!
            .set(userDocument, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                Log.d("SignInViewModel", "✅ User document created/updated successfully!")
                Log.d("SignInViewModel", "   UserId: $userId")
                Log.d("SignInViewModel", "   Email: $email")
                Log.d("SignInViewModel", "   Username: ${userData.username}")
                Log.d("SignInViewModel", "   Path: users/$userId")
            }
            .addOnFailureListener { e ->
                Log.e("SignInViewModel", "❌ Error creating user document", e)
            }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

    /**
     * Helper functions untuk mendapatkan info user yang sedang login
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun getCurrentUserName(): String? {
        return auth.currentUser?.displayName
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
}