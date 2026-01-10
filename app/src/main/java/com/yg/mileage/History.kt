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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.PendingActions
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yg.mileage.data.TripGroupEntity
import com.yg.mileage.ui.theme.MyMileageShapeDefaults
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripLogScreen(
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel,
    onNavigateToTripDetails: () -> Unit
) {
    val trips by carViewModel.savedTrips.collectAsState()
    val tripGroups by carViewModel.tripGroups.collectAsState()
    val defaultCurrency by carViewModel.defaultCurrency.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showNewGroupDialog by remember { mutableStateOf(false) }

    if (showNewGroupDialog) {
        NewGroupDialog(
            onDismiss = { showNewGroupDialog = false },
            onConfirm = { groupName ->
                coroutineScope.launch {
                    carViewModel.addTripGroup(groupName)
                }
                showNewGroupDialog = false
            }
        )
    }

    TripLogContent(
        modifier = modifier,
        trips = trips,
        tripGroups = tripGroups,
        defaultCurrency = defaultCurrency,
        onNavigateToTripDetails = onNavigateToTripDetails,
        onEditTrip = { trip ->
            carViewModel.setEditingTrip(trip)
            // If it's part of a group, we might want to set the group ID, but the trip already has it.
            // TripDetails logic will handle loading from the trip.
            carViewModel.setEditingTripGroupId(trip.tripGroupId)
            onNavigateToTripDetails()
        },
        onDeleteTrip = { tripId ->
            coroutineScope.launch {
                carViewModel.deleteTrip(tripId)
            }
        },
        onNewTrip = {
            carViewModel.setEditingTrip(null)
            carViewModel.setEditingTripGroupId(null)
            onNavigateToTripDetails()
        },
        onNewGroupTrip = {
            showNewGroupDialog = true
        },
        onAddTripToGroup = { groupId ->
            carViewModel.setEditingTrip(null)
            carViewModel.setEditingTripGroupId(groupId)
            onNavigateToTripDetails()
        }
    )
}

@Composable
fun NewGroupDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Group Trip") },
        text = {
            Column {
                Text("Enter a name for this group trip:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (groupName.isNotBlank()) onConfirm(groupName) },
                enabled = groupName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TripLogContent(
    modifier: Modifier = Modifier,
    trips: List<Trip>,
    tripGroups: List<TripGroupEntity>,
    defaultCurrency: Currency?,
    onNavigateToTripDetails: () -> Unit,
    onEditTrip: (Trip) -> Unit,
    onDeleteTrip: (String) -> Unit,
    onNewTrip: () -> Unit,
    onNewGroupTrip: () -> Unit,
    onAddTripToGroup: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    var filterIndex by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Prepare list items
    val historyItems = remember(trips, tripGroups, filterIndex) {
        val filteredTrips = when (filterIndex) {
            1 -> trips.filter { it.status == TripStatus.COMPLETED }
            2 -> trips.filter { it.status == TripStatus.DRAFT }
            else -> trips
        }

        val items = mutableListOf<HistoryItem>()
        val tripsByGroup = filteredTrips.filter { it.tripGroupId != null }.groupBy { it.tripGroupId!! }
        val orphanTrips = filteredTrips.filter { it.tripGroupId == null }

        // Process Groups
        tripGroups.forEach { group ->
            val groupTrips = tripsByGroup[group.id]?.sortedByDescending { it.updatedAt } ?: emptyList()

            // Only add group if filter is ALL or if it has relevant items
            // Requirement says: "Group trips created... will be listed... alongside normal trips"
            // If we filter by 'Done', maybe we only show groups that have done trips? Or just show groups?
            // Assuming we show groups always if they match criteria, but for now showing all groups + filtered content within them

            val hasContent = groupTrips.isNotEmpty()

            // Logic: Create a group block.
            // The timestamp for sorting the block could be the group's updatedAt or the latest trip in it.
            // Let's use group's updatedAt.

            // We'll create a temporary structure to sort later
        }

        val tempItems = mutableListOf<SortableHistoryItem>()

        // Add Groups
        tripGroups.forEach { group ->
            val groupTrips = tripsByGroup[group.id]?.sortedByDescending { it.updatedAt } ?: emptyList()

            // If we have a filter active, and the group has no trips matching that filter, should we hide the group?
            // If filter is DRAFT, and group has no drafts, maybe hide it?
            // For now, let's show the group if it exists, but only show matching trips inside.

            // Actually, if filter is applied, we should probably only show groups that have matching trips OR if the group itself is new (empty).
            // But 'orphanTrips' handles non-grouped.
            // Let's stick to: Show group header, then matching trips.

            val sortTime = group.updatedAt.time.coerceAtLeast(groupTrips.firstOrNull()?.updatedAt?.time ?: 0L)

            tempItems.add(SortableHistoryItem.GroupBlock(group, groupTrips, sortTime))
        }

        // Add Orphan Trips
        orphanTrips.forEach { trip ->
            tempItems.add(SortableHistoryItem.SingleTripItem(trip, trip.updatedAt.time))
        }

        // Sort by time descending
        tempItems.sortByDescending { it.time }

        // Flatten to HistoryItem list
        tempItems.forEach { item ->
            when (item) {
                is SortableHistoryItem.SingleTripItem -> {
                    items.add(HistoryItem.SingleTrip(item.trip))
                }
                is SortableHistoryItem.GroupBlock -> {
                    items.add(HistoryItem.GroupHeader(item.group))
                    item.trips.forEachIndexed { index, trip ->
                        val isLast = index == item.trips.lastIndex
                        items.add(HistoryItem.GroupTrip(trip, isLast))
                    }
                }
            }
        }
        items
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            FilterBottomSheetContent(onApply = {
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            })
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
            TripHistoryFilterSegmented(
                selectedIndex = filterIndex,
                onSelected = { filterIndex = it },
                onFilterClick = { showBottomSheet = true }
            )

            Spacer(Modifier.height(12.dp)) // space between filter and list

            if (historyItems.isEmpty()) {
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
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    // Remove default spacing as we need tight packing for groups, handle spacing manually or via items
                    // Actually, grouped items need 0 spacing between them?
                    // "trips... listed in middlelistitemshape and bottomListItemShape"
                    // This implies they connect visually.
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(historyItems) { item ->
                        when (item) {
                            is HistoryItem.SingleTrip -> {
                                Box(modifier = Modifier.padding(vertical = 7.dp)) { // Add spacing around independent cards
                                    TripCard(
                                        trip = item.trip,
                                        dateFormat = dateFormat,
                                        defaultCurrency = defaultCurrency,
                                        onEdit = { onEditTrip(item.trip) },
                                        onDelete = { onDeleteTrip(item.trip.id) },
                                        shape = MyMileageShapeDefaults.cardShape
                                    )
                                }
                            }
                            is HistoryItem.GroupHeader -> {
                                Box(modifier = Modifier.padding(top = 14.dp)) { // Add spacing before group start
                                    GroupHeaderItem(
                                        group = item.group,
                                        onAddTrip = { onAddTripToGroup(item.group.id) }
                                    )
                                }
                            }
                            is HistoryItem.GroupTrip -> {
                                val shape = if (item.isLast) MyMileageShapeDefaults.bottomListItemShape() else MyMileageShapeDefaults.middleListItemShape()
                                TripCard(
                                    trip = item.trip,
                                    dateFormat = dateFormat,
                                    defaultCurrency = defaultCurrency,
                                    onEdit = { onEditTrip(item.trip) },
                                    onDelete = { onDeleteTrip(item.trip.id) },
                                    shape = shape
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for FAB
                    }
                }
            }
        }

        // Floating Action Button for adding new trip
        Box(Modifier.fillMaxSize()) {
            var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

            FloatingActionButtonMenu(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                expanded = fabMenuExpanded,
                button = {
                    val containerColor = MaterialTheme.colorScheme.primaryContainer
                    ToggleFloatingActionButton(
                        modifier = Modifier
                            .semantics {
                                stateDescription = if (fabMenuExpanded) "Expanded" else "Collapsed"
                                contentDescription = "Toggle menu"
                            }
                            .animateFloatingActionButton(
                                visible = true, alignment = Alignment.BottomEnd
                            ),
                        checked = fabMenuExpanded,
                        containerColor = { containerColor }, // Fixed: Capture color outside the lambda
                        onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }
                    ) {
                        val imageVector by remember {
                            derivedStateOf {
                                if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                            }
                        }
                        Icon(
                            painter = rememberVectorPainter(imageVector),
                            contentDescription = null,
                            modifier = Modifier.animateIcon({ checkedProgress })
                        )
                    }
                }
            ) {
                // --- Menu Content ---

                // Item 1: New Trip
                FloatingActionButtonMenuItem(
                    onClick = {
                        fabMenuExpanded = false
                        onNewTrip()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add New Trip"
                        )
                    },
                    text = { Text(text = "New Trip") },
                )

                // --- Item 2: New Group Trip ---
                FloatingActionButtonMenuItem(
                    onClick = {
                        fabMenuExpanded = false
                        onNewGroupTrip()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.GroupAdd,
                            contentDescription = "New Group Trip"
                        )
                    },
                    text = { Text(text = "New Group Trip") },
                )
            }
        }
    }
}

@Composable
fun GroupHeaderItem(
    group: TripGroupEntity,
    onAddTrip: () -> Unit
) {
    Surface(
        shape = MyMileageShapeDefaults.topListItemShape(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = group.groupName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            FilledTonalIconButton(
                onClick = onAddTrip,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Trip to Group",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun TripHistoryFilterSegmented(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    onFilterClick: () -> Unit
) {
    val options = listOf("All", "Done", "Draft")
    val checkedIcons = listOf(
        Icons.AutoMirrored.Filled.List,
        Icons.Filled.Done,
        Icons.Filled.PendingActions
    )
    val unCheckedIcons = listOf(
        Icons.AutoMirrored.Rounded.List,
        Icons.Outlined.Done,
        Icons.Rounded.PendingActions
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
                modifier = Modifier.weight(1f),
                shapes =
                when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                }
            ) {
                Icon(
                    if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index],
                    contentDescription = label
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(label)
            }
        }
        FilledTonalIconButton(
            onClick = onFilterClick,
            shape = IconButtonDefaults.smallSquareShape,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.FilterList,
                contentDescription = "Filter"
            )
        }
    }
}

@Composable
fun TripCard(
    trip: Trip,
    dateFormat: SimpleDateFormat,
    defaultCurrency: Currency?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    shape: CornerBasedShape
) {
    val isDraft = trip.status == TripStatus.DRAFT
    val cardColor = if (isDraft)
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.13f)
    else
        MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trip.vehicleName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDraft) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(onClick = { /* Share logic */ } ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

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
            trip.fuelCost?.let { cost ->
                val currencySymbol = defaultCurrency?.symbol ?: trip.currencyId ?: ""
                Text(
                    text = "Fuel Cost: $currencySymbol${String.format(Locale.getDefault(), "%.2f", cost)}",
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
    }
}

@Composable
fun FilterBottomSheetContent(onApply: () -> Unit) {
    Spacer(modifier = Modifier.padding(25.dp))
    Text(text = "                              :)")
    Spacer(modifier = Modifier.padding(25.dp))
}

sealed class HistoryItem {
    data class SingleTrip(val trip: Trip) : HistoryItem()
    data class GroupHeader(val group: TripGroupEntity) : HistoryItem()
    data class GroupTrip(val trip: Trip, val isLast: Boolean) : HistoryItem()
}

sealed class SortableHistoryItem(val time: Long) {
    class SingleTripItem(val trip: Trip, time: Long) : SortableHistoryItem(time)
    class GroupBlock(val group: TripGroupEntity, val trips: List<Trip>, time: Long) : SortableHistoryItem(time)
}

@Preview(showBackground = true)
@Composable
fun HistoryPreview() {
    val sampleTrips = listOf(
        Trip(
            id = "1",
            vehicleId = "v1",
            vehicleName = "Fortuner",
            startMileage = 10000.0,
            endMileage = 10500.0,
            fuelFilled = 50.0,
            tripDistance = 500.0,
            fuelEfficiency = 10.0,
            fuelCost = 5000.0,
            fuelPricePerUnit = 100.0,
            currencyId = "INR",
            status = TripStatus.COMPLETED,
            createdAt = Date(),
            updatedAt = Date()
        ),
        Trip(
            id = "2",
            vehicleId = "v1",
            vehicleName = "Fortuner",
            startMileage = 10500.0,
            endMileage = null,
            fuelFilled = 40.0,
            tripDistance = null,
            fuelEfficiency = null,
            fuelCost = null,
            fuelPricePerUnit = 110.0,
            currencyId = "INR",
            status = TripStatus.DRAFT,
            createdAt = Date(),
            updatedAt = Date()
        )
    )
    MaterialTheme {
        TripLogContent(
            trips = sampleTrips,
            tripGroups = emptyList(),
            defaultCurrency = Currency("inr", "INR", "Indian Rupee", "₹", true),
            onNavigateToTripDetails = {},
            onEditTrip = {},
            onDeleteTrip = {},
            onNewTrip = {},
            onNewGroupTrip = {},
            onAddTripToGroup = {}
        )
    }
}
