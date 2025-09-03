package com.yg.mileage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SecuritySettingsScreen(carViewModel: CarViewModel) {
    val currentUser by carViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Security & Sign-in",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        currentUser?.let { user ->
            Text(
                text = "Account: ${user.email ?: "N/A"}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Placeholder security options
            SecurityOptionItem(title = "Change Password") {
                // TODO: Implement change password functionality
            }
            SecurityOptionItem(title = "Two-Step Verification") {
                // TODO: Implement 2SV setup
            }
            SecurityOptionItem(title = "Manage Devices") {
                // TODO: Implement device management
            }
            SecurityOptionItem(title = "Recent Security Activity") {
                // TODO: Implement recent activity display
            }

        } ?: run {
            Text("User not signed in or information unavailable.")
        }
    }
}

@Composable
private fun SecurityOptionItem(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxSize(0.8f).padding(vertical = 8.dp)
    ) {
        Text(title)
    }
}

