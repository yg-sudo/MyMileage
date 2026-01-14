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

@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.yg.mileage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.yg.mileage.ui.theme.MyMileageShapeDefaults

@Composable
fun PersonalInfoScreen(carViewModel: CarViewModel) {
    val currentUser by carViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        currentUser?.let { user ->
            if (user.profilePictureUrl != null) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    contentDescription = "Vehicles Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(MaterialShapes.Cookie9Sided.toShape()),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = rememberVectorPainter(Icons.Default.AccountCircle),
                    contentDescription = "Default Vehicles Picture",
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MyMileageShapeDefaults.cardShape, // <-- use new card shape
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.5f
                )
            )
        ) {
        }
        // Top item - use topListItemShape and make the whole surface clickable
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MyMileageShapeDefaults.topListItemShape(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            onClick = { TODO() }
        ) {
        }

        // Divider between rows (Tomato used a subtle separator)
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 0.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.surface
        )

        // Bottom item - use bottomListItemShape and clickable
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MyMileageShapeDefaults.middleListItemShape(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            onClick = { TODO() }
        ) {
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 0.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.surface
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MyMileageShapeDefaults.bottomListItemShape(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            onClick = { TODO() }
        ) {
        }
    }
}
