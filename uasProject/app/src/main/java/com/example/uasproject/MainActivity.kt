package com.example.uasproject

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uasproject.data.model.GoogleAuthUiClient
import com.example.uasproject.presentation.matches.MatchListContent
import com.example.uasproject.presentation.profile.ProfileScreen
import com.example.uasproject.presentation.sign_in.SignInScreen
import com.example.uasproject.presentation.sign_in.SignInViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel = viewModel<SignInViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    // State untuk menentukan screen mana yang ditampilkan
                    var isAuthenticated by remember {
                        mutableStateOf(googleAuthUiClient.getSignedInUser() != null)
                    }

                    // State untuk navigasi ke profile
                    var showProfile by remember { mutableStateOf(false) }

                    // Handle sign in success
                    LaunchedEffect(state.isSignInSuccessfull) {
                        if (state.isSignInSuccessfull) {
                            Toast.makeText(
                                this@MainActivity,
                                "Sign in successful",
                                Toast.LENGTH_LONG
                            ).show()
                            isAuthenticated = true
                            viewModel.resetState()
                        }
                    }

                    // Tampilkan screen berdasarkan authentication status dan navigasi
                    when {
                        !isAuthenticated -> {
                            // User belum login, tampilkan SignInScreen
                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthUiClient.signIn()
                                        viewModel.onSignInResult(signInResult)
                                    }
                                }
                            )
                        }
                        showProfile -> {
                            // Tampilkan ProfileScreen
                            ProfileScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        isAuthenticated = false
                                        showProfile = false
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Berhasil keluar",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onBack = {
                                    showProfile = false
                                }
                            )
                        }
                        else -> {
                            // User sudah login, tampilkan MatchListContent
                            MatchListContent(
                                onLogout = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        isAuthenticated = false
                                        showProfile = false
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Signed out successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onNavigateToProfile = {
                                    showProfile = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}