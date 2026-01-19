/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2025  Yojit Ghadi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.yg.mileage.auth

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.yg.mileage.R
import com.yg.mileage.data.Repository
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?,
    val email: String?
)

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

class FirebaseAuthClient(
    private val context: Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val repository: Repository
) {
    private val credentialManager = CredentialManager.create(context)

    fun getSignedInUser(): UserData? = auth.currentUser?.toUserData()

    // Get Google Identity token for Drive backup
    suspend fun getGoogleIdToken(activity: Activity): String? {
        val webClientId = context.getString(R.string.default_web_client_id)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )

            if (result.credential is CustomCredential && result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                googleIdTokenCredential.idToken
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    // Google Sign-In with Credential Manager
    suspend fun signInWithGoogle(activity: Activity): SignInResult {
        val webClientId = context.getString(R.string.default_web_client_id)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )

            handleSignIn(result.credential)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            val message = when (e) {
                is GetCredentialException -> {
                    val typeMessage = when {
                        e.type.contains("user_canceled", ignoreCase = true) -> "User canceled"
                        e.type.contains("no_credential", ignoreCase = true) -> "No credential found"
                        e.type.contains("invalid_credential", ignoreCase = true) -> "Invalid credential type"
                        e.type.contains("interrupted", ignoreCase = true) -> "Interrupted"
                        e.type.contains("network_error", ignoreCase = true) -> "Network error"
                        e.type.contains("internal_error", ignoreCase = true) -> "Internal error"
                        else -> "Unknown error: ${e.type}"
                    }
                    "Google sign-in failed: $typeMessage. ${e.message ?: e.localizedMessage ?: ""}".trim()
                }
                else -> "Google sign-in failed: ${e.localizedMessage ?: e.message ?: e.toString()}"
            }
            SignInResult(data = null, errorMessage = message)
        }
    }

    // Microsoft Sign-In
    suspend fun signInWithMicrosoft(activity: Activity): SignInResult {
        return try {
            val provider = OAuthProvider.newBuilder("microsoft.com").apply {
                scopes = listOf("User.Read")
                addCustomParameter("prompt", "select_account")
                addCustomParameter("tenant", "consumers")
            }.build()

            val result = auth.startActivityForSignInWithProvider(activity, provider).await()
            SignInResult(data = result.user?.toUserData(), errorMessage = null)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.localizedMessage ?: e.message)
        }
    }

    private suspend fun handleSignIn(credential: Credential): SignInResult {
        // Check if credential is of type Google ID
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                // Sign in to Firebase using the token
                return firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
            } catch (e: GoogleIdTokenParsingException) {
                return SignInResult(data = null, errorMessage = "Failed to parse Google ID token: ${e.localizedMessage ?: e.message}")
            }
        } else {
            return SignInResult(data = null, errorMessage = "Credential is not of type Google ID")
        }
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String): SignInResult {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            user?.let {
                it.email?.let { it1 -> repository.restoreFromDrive(it.uid, it1) }
            }
            SignInResult(data = user?.toUserData(), errorMessage = null)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.localizedMessage ?: e.message)
        }
    }

    // Email/Password Sign-In
    suspend fun signInWithEmailPassword(email: String, password: String): SignInResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            SignInResult(data = result.user?.toUserData(), errorMessage = null)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.localizedMessage ?: e.message)
        }
    }

    // Email/Password Sign-Up
    suspend fun createUserWithEmailPassword(email: String, password: String): SignInResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            SignInResult(data = result.user?.toUserData(), errorMessage = null)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.localizedMessage ?: e.message)
        }
    }

    // Password Reset
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    // Phone Sign-In
    fun verifyPhoneNumber(
        activity: Activity,
        phoneNumber: String,
        onCodeSent: (String) -> Unit,
        onVerificationFailed: (FirebaseException) -> Unit
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-retrieval may sign the user in, handle this if needed
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onVerificationFailed(e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    onCodeSent(verificationId)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun signInWithPhoneCredential(verificationId: String, code: String): SignInResult {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        return try {
            val user = auth.signInWithCredential(credential).await().user
            SignInResult(data = user?.toUserData(), errorMessage = null)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.localizedMessage ?: e.message)
        }
    }

    suspend fun signOut() {
        try {
            // Firebase sign out
            auth.signOut()

            // Clear credential state from all credential providers
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            // Log error but don't fail sign out
            if (e is ClearCredentialException) {
                // Handle clear credential exception if needed
            }
        }
    }

    private fun FirebaseUser.toUserData(): UserData =
        UserData(
            userId = uid,
            username = displayName,
            profilePictureUrl = photoUrl?.toString(),
            email = email
        )
}
