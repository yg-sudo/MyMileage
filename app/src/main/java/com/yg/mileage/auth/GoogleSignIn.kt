package com.yg.mileage.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
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
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun getSignedInUser(): UserData? = auth.currentUser?.toUserData()

    // Google Sign-In
    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account = task.await()
            val idToken = account.idToken ?: return SignInResult(
                data = null,
                errorMessage = "Google sign-in failed: ID token was null."
            )
            GoogleAuthProvider.getCredential(idToken, null)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            return SignInResult(data = null, errorMessage = e.message)
        }

        return try {
            val user = auth.signInWithCredential(credential).await().user
            SignInResult(data = user?.toUserData(), errorMessage = null)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.message)
        }
    }

    // Email/Password Sign-In
    suspend fun signInWithEmailPassword(email: String, password: String): SignInResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            SignInResult(data = result.user?.toUserData(), errorMessage = null)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.message)
        }
    }

    // Email/Password Sign-Up
    suspend fun createUserWithEmailPassword(email: String, password: String): SignInResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            SignInResult(data = result.user?.toUserData(), errorMessage = null)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.message)
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
            SignInResult(data = null, errorMessage = e.message)
        }
    }


    suspend fun signOut() {
        try {
            auth.signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("9981449638816-0a9mvq58gs6rangcqaibrenfl0jhdj4l.apps.googleusercontent.com")
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInClient.signOut().await()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
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