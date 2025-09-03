@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.yg.mileage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yg.mileage.auth.UserData

// Main entry point for the screen
@Composable
fun AccountScreen(
    currentUser: UserData?,
    onSignOutClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onEmailSignInClick: (String, String) -> Unit,
    onEmailSignUpClick: (String, String) -> Unit,
    onSendOtpClick: (String) -> Unit,
    onVerifyOtpClick: (String) -> Unit,
    onNavigateToPersonalInfo: () -> Unit, // New parameter
    onNavigateToSecurity: () -> Unit     // New parameter
) {
    if (currentUser != null) {
        SignedInAccountScreen(
            currentUser = currentUser,
            onSignOutClick = onSignOutClick,
            onNavigateToPersonalInfo = onNavigateToPersonalInfo, // Passed through
            onNavigateToSecurity = onNavigateToSecurity         // Passed through
        )
    } else {
        SignedOutAccountScreen(
            onGoogleSignInClick = onGoogleSignInClick,
            onEmailSignInClick = onEmailSignInClick,
            onEmailSignUpClick = onEmailSignUpClick,
            onSendOtpClick = onSendOtpClick,
            onVerifyOtpClick = onVerifyOtpClick
        )
    }
}

// Screen for when the user IS signed in
@Composable
fun SignedInAccountScreen(
    currentUser: UserData,
    onSignOutClick: () -> Unit,
    onNavigateToPersonalInfo: () -> Unit, // New parameter
    onNavigateToSecurity: () -> Unit     // New parameter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Header
        if (currentUser.profilePictureUrl != null) {
            AsyncImage(
                model = currentUser.profilePictureUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(96.dp)
                    .clip(MaterialShapes.Cookie12Sided.toShape()),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Default Profile Picture",
                modifier = Modifier
                    .size(96.dp)
                    .clip(MaterialShapes.Cookie12Sided.toShape())

            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = currentUser.username ?: "User",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        if (currentUser.email != null) {
            Text(
                text = currentUser.email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Account Info List
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column {
                AccountInfoRow(
                    icon = Icons.Default.Person,
                    title = "Personal info",
                    subtitle = "Name, email, phone, profiles",
                    iconBackgroundColor = Color(0xFFE0F7FA),
                    onClick = onNavigateToPersonalInfo // Used here
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )
                AccountInfoRow(
                    icon = Icons.Default.Security,
                    title = "Security & sign-in",
                    subtitle = "Google password, recent security events",
                    iconBackgroundColor = Color(0xFFE8EAF6),
                    onClick = onNavigateToSecurity // Used here
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

        ) {
            AccountInfoRow(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "Sign out",
                subtitle = "Sign out of your Google Account",
                iconBackgroundColor = Color(0xFFFBE9E7),
                onClick = onSignOutClick
            )
        }
    }
}

@Composable
fun AccountInfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBackgroundColor: Color,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(MaterialShapes.Cookie12Sided.toShape())
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = Color.Black.copy(alpha = 0.7f)
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}


// Screen for when the user is NOT signed in
@Composable
fun SignedOutAccountScreen(
    onGoogleSignInClick: () -> Unit,
    onEmailSignInClick: (String, String) -> Unit,
    onEmailSignUpClick: (String, String) -> Unit,
    onSendOtpClick: (String) -> Unit,
    onVerifyOtpClick: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Google", "Email", "Phone")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Sign In",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        when (selectedTabIndex) {
            0 -> GoogleSignInTab(onGoogleSignInClick)
            1 -> EmailSignInTab(onEmailSignInClick, onEmailSignUpClick)
            2 -> PhoneSignInTab(onSendOtpClick, onVerifyOtpClick)
        }
    }
}

@Composable
fun GoogleSignInTab(onGoogleSignInClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Sign in with your Google Account to continue.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onGoogleSignInClick) {
            Text("Sign in with Google")
        }
    }
}

@Composable
fun EmailSignInTab(
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { onSignIn(email, password) }) {
                Text("Sign In")
            }
            Button(onClick = { onSignUp(email, password) }) {
                Text("Sign Up")
            }
        }
    }
}

@Composable
fun PhoneSignInTab(
    onSendOtp: (String) -> Unit,
    onVerifyOtp: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number (e.g. +16505551234)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = !isCodeSent
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onSendOtp(phoneNumber)
                isCodeSent = true
            },
            enabled = !isCodeSent
        ) {
            Text("Send Code")
        }
        Text(
            text = "Work In Progress",
            style = MaterialTheme.typography.bodySmall,
        )
    }
        if (isCodeSent) {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("Verification Code") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onVerifyOtp(otp) }) {
                Text("Verify & Sign In")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Work In Progress",
                style = MaterialTheme.typography.bodySmall)
        }
}


// --- PREVIEWS ---
@Preview(showBackground = true, name = "Signed In")
@Composable
fun AccountScreenPreview_UserLoggedIn() {
    val user = UserData(userId = "123", username = "John Doe", profilePictureUrl = null, email = "john.doe@example.com")
    AccountScreen(
        currentUser = user,
        onSignOutClick = {},
        onGoogleSignInClick = {},
        onEmailSignInClick = {_,_ ->},
        onEmailSignUpClick = {_,_ ->},
        onSendOtpClick = {},
        onVerifyOtpClick = {},
        onNavigateToPersonalInfo = {}, // Added for preview
        onNavigateToSecurity = {}     // Added for preview
    )
}

@Preview(showBackground = true, name = "Signed Out - Google")
@Composable
fun AccountScreenPreview_UserLoggedOut() {
    AccountScreen(
        currentUser = null,
        onSignOutClick = {},
        onGoogleSignInClick = {},
        onEmailSignInClick = {_,_ ->},
        onEmailSignUpClick = {_,_ ->},
        onSendOtpClick = {},
        onVerifyOtpClick = {},
        onNavigateToPersonalInfo = {}, // Added for preview
        onNavigateToSecurity = {}     // Added for preview
    )
}