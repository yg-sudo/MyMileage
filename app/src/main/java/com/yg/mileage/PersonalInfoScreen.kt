@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.yg.mileage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PersonalInfoScreen(carViewModel: CarViewModel) {
    val currentUser by carViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        currentUser?.let { user ->
            if (user.profilePictureUrl != null) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(MaterialShapes.Ghostish.toShape()),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = rememberVectorPainter(Icons.Default.AccountCircle),
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier.size(120.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = user.username ?: "N/A",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = user.email ?: "No email provided",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        } ?: run {
            Text("No user information available.", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
