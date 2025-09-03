package com.yg.mileage

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripLogScreen(
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel,
    onNavigateToTripDetails: () -> Unit
) {
    val trips by carViewModel.savedTrips.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    var filterIndex by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope() // <---- ADD THIS LINE

    // Filtered trips according to filter selection
    val filteredTrips = remember(trips, filterIndex) {
        when (filterIndex) {
            1 -> trips.filter { it.status == TripStatus.COMPLETED }
            2 -> trips.filter { it.status == TripStatus.DRAFT }
            else -> trips
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // FILTER SEGMENTED BUTTONS AT THE TOP
            @OptIn(ExperimentalMaterial3ExpressiveApi::class)
            TripHistoryFilterSegmented(
                selectedIndex = filterIndex,
                onSelected = { filterIndex = it }
            )

            Spacer(Modifier.height(12.dp)) // space between filter and list

            if (filteredTrips.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trips to display.\nCreate or change your filter!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Expressive individual cards spaced apart
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    filteredTrips.sortedByDescending { it.updatedAt }.forEach { trip ->
                        TripCard(
                            trip = trip,
                            dateFormat = dateFormat,
                            onEdit = {
                                carViewModel.setEditingTrip(trip)
                                onNavigateToTripDetails()
                            },
                            onDelete = {
                                coroutineScope.launch { // <---- FIX: wrap in coroutine
                                    carViewModel.deleteTrip(trip.id)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button for adding new trip
        FloatingActionButton(
            onClick = {
                carViewModel.setEditingTrip(null) // Clear any existing editing trip
                onNavigateToTripDetails()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add New Trip"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripHistoryFilterSegmented(
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    val options = listOf("All", "Done", "Draft")
    val checkedIcons = listOf(
        Icons.AutoMirrored.Filled.List,
        Icons.Filled.Done,
        Icons.Filled.PendingActions
    )
    val unCheckedIcons = listOf(
        Icons.AutoMirrored.Outlined.List,
        Icons.Outlined.Done,
        Icons.Outlined.PendingActions
    )

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { onSelected(index) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index],
                    contentDescription = label
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(label)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripCard(
    trip: Trip,
    dateFormat: SimpleDateFormat,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isDraft = trip.status == TripStatus.DRAFT
    val cardColor = if (isDraft)
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.13f)
    else
        MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            draggedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = trip.vehicleName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDraft) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isDraft) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 1.dp)
                ) {
                    Text(
                        text = trip.status.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDraft) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = "Start: ${trip.startMileage ?: "--"} km • End: ${trip.endMileage ?: "--"} km",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Fuel: ${trip.fuelFilled ?: "--"} Ltr",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                trip.tripDistance?.let {
                    Text(
                        text = "Distance: $it km",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                trip.fuelEfficiency?.let {
                    Text(
                        text = "Efficiency: $it km/Ltr",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Updated: ${dateFormat.format(trip.updatedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
