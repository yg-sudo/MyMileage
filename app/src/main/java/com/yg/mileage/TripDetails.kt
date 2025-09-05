@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.yg.mileage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.UUID

// --- Use your own message type enum ---
enum class AppMessageType { ERROR, WARNING, SUCCESS, INFO }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MileageCalculatorScreen(
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel,
    googleAccount: GoogleSignInAccount?
) {
    var startMileageText by remember { mutableStateOf("") }
    var endMileageText by remember { mutableStateOf("") }
    var fuelFilledText by remember { mutableStateOf("") }
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var vehicleDropdownExpanded by remember { mutableStateOf(false) }
    var tripDistance by remember { mutableStateOf<Double?>(null) }
    var customCalculationResult by remember { mutableStateOf<Double?>(null) }
    var fuelCost by remember { mutableStateOf<Double?>(null) }
    var messageType by remember { mutableStateOf<AppMessageType?>(null) }
    var messageText by remember { mutableStateOf("") }
    var showMessage by remember { mutableStateOf(false) }
    var isCalculating by remember { mutableStateOf(false) }

    val editingTrip by carViewModel.editingTrip.collectAsState()
    val defaultCurrency by carViewModel.defaultCurrency.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var currentTripId by rememberSaveable(editingTrip?.id) { mutableStateOf(editingTrip?.id) }
    var isTripInProgress by rememberSaveable(editingTrip != null) { mutableStateOf(editingTrip != null) }

    fun showMessage(type: AppMessageType, text: String) {
        messageType = type
        messageText = text
        showMessage = true
    }

    fun generateUniqueTripId(): String = UUID.randomUUID().toString()
    val decimalFormat = remember { DecimalFormat("#,##0.0##") }
    val savedVehicles by carViewModel.savedVehicles.collectAsState()

    val fuelUnit = when (selectedVehicle?.fuelType) {
        FuelType.PETROL, FuelType.DIESEL -> "Ltr"
        FuelType.CNG -> "KG"
        null -> "Ltr"
    }

    LaunchedEffect(editingTrip) {
        editingTrip?.let { trip ->
            selectedVehicle = savedVehicles.find { it.name == trip.vehicleName }
            startMileageText = trip.startMileage?.toString() ?: ""
            endMileageText = trip.endMileage?.toString() ?: ""
            fuelFilledText = trip.fuelFilled?.toString() ?: ""
            tripDistance = trip.tripDistance
            customCalculationResult = trip.fuelEfficiency
            fuelCost = trip.fuelCost
        } ?: run {
            startMileageText = ""
            endMileageText = ""
            fuelFilledText = ""
            tripDistance = null
            customCalculationResult = null
            fuelCost = null
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            ExposedDropdownMenuBox(
                expanded = vehicleDropdownExpanded,
                onExpandedChange = { vehicleDropdownExpanded = !vehicleDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedVehicle?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Vehicle Profile *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vehicleDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = vehicleDropdownExpanded,
                    onDismissRequest = { vehicleDropdownExpanded = false }
                ) {
                    if (savedVehicles.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No vehicles available") },
                            onClick = { vehicleDropdownExpanded = false }
                        )
                    } else {
                        savedVehicles.forEach { vehicle ->
                            DropdownMenuItem(
                                text = { Text(vehicle.name) },
                                onClick = {
                                    selectedVehicle = vehicle
                                    vehicleDropdownExpanded = false
                                    tripDistance = null
                                    customCalculationResult = null
                                    fuelCost = null
                                    showMessage = false
                                    if (editingTrip != null) {
                                        carViewModel.setEditingTrip(null)
                                    }
                                    startMileageText = ""
                                    endMileageText = ""
                                    fuelFilledText = ""
                                }
                            )
                        }
                    }
                }
            }
        }
        if (selectedVehicle != null) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                OutlinedTextField(
                    value = startMileageText,
                    onValueChange = {
                        startMileageText = it
                        tripDistance = null
                        customCalculationResult = null
                        fuelCost = null
                        showMessage = false
                    },
                    label = { Text("Trip Start") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = endMileageText,
                    onValueChange = {
                        endMileageText = it
                        tripDistance = null
                        customCalculationResult = null
                        fuelCost = null
                        showMessage = false
                    },
                    label = { Text("Trip End") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                val fuelLabel = when (selectedVehicle?.fuelType) {
                    FuelType.PETROL -> "Petrol Filled (Ltr)"
                    FuelType.DIESEL -> "Diesel Filled (Ltr)"
                    FuelType.CNG -> "CNG Filled (KG)"
                    null -> "Fuel Filled"
                }
                OutlinedTextField(
                    value = fuelFilledText,
                    onValueChange = { itInput ->
                        if (itInput.isEmpty() || itInput.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                            fuelFilledText = itInput
                            tripDistance = null
                            customCalculationResult = null
                            fuelCost = null
                            showMessage = false
                        }
                    },
                    label = { Text(fuelLabel) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter fuel filled (e.g. 5.25)") }
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            val start = startMileageText.toDoubleOrNull()
                            val end = endMileageText.toDoubleOrNull()
                            val fuel = fuelFilledText.toDoubleOrNull()

                            if (selectedVehicle == null) {
                                showMessage(AppMessageType.ERROR, "Please select a vehicle profile.")
                                return@Button
                            }
                            if (start == null || end == null) {
                                showMessage(AppMessageType.ERROR, "Start and End mileage must be valid numbers.")
                                return@Button
                            }
                            if (start < 0 || end < 0) {
                                showMessage(AppMessageType.ERROR, "Mileage cannot be negative.")
                                return@Button
                            }
                            if (end <= start) {
                                showMessage(AppMessageType.ERROR, "End mileage must be greater than start mileage.")
                                return@Button
                            }
                            if (fuel == null || fuel <= 0) {
                                showMessage(AppMessageType.ERROR, "Fuel filled must be a positive number.")
                                return@Button
                            }

                            isCalculating = true
                            coroutineScope.launch(Dispatchers.Default) {
                                val distance = end - start
                                val efficiency = distance / fuel
                                
                                // Calculate fuel cost if we have fuel price data - fixed smart cast issues
                                var calculatedFuelCost: Double? = null
                                
                                val vehicle = selectedVehicle
                                val fuelType = vehicle?.fuelType
                                if (fuelType != null) {
                                    val latestFuelPrice = carViewModel.getLatestFuelPrice(fuelType)
                                    if (latestFuelPrice != null) {
                                        calculatedFuelCost = fuel * latestFuelPrice.pricePerUnit
                                    }
                                }
                                
                                delay(1000)
                                tripDistance = distance
                                customCalculationResult = efficiency
                                fuelCost = calculatedFuelCost
                                showMessage(AppMessageType.SUCCESS, "Calculation complete!")
                                isCalculating = false
                            }
                        },
                        enabled = true,
                        contentPadding = ButtonDefaults.ContentPadding
                    ) {
                        Icon(Icons.Filled.Calculate, contentDescription = "Calculate Mileage")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculate")
                    }

                    Button(
                        onClick = {
                            val start = startMileageText.toDoubleOrNull()
                            val end = endMileageText.toDoubleOrNull()
                            val fuel = fuelFilledText.toDoubleOrNull()
                            val vehicle = selectedVehicle

                            if (vehicle == null) {
                                showMessage(AppMessageType.ERROR, "Please select a vehicle profile.")
                                return@Button
                            }

                            // Only logical validation (allow drafts)
                            if (start != null && end != null && end < start) {
                                showMessage(AppMessageType.ERROR, "End mileage cannot be less than start mileage.")
                                return@Button
                            }

                            val isComplete = start != null && end != null && fuel != null && fuel > 0 && end >= start

                            // Auto-calc if complete and result missing
                            if (isComplete && (tripDistance == null || customCalculationResult == null)) {
                                val distance = end!! - start!!
                                val efficiency = distance / fuel!!
                                tripDistance = distance
                                customCalculationResult = efficiency
                                
                                // Calculate fuel cost if we have fuel price data - fixed smart cast issues
                                val vehicle = selectedVehicle
                                val fuelType = vehicle?.fuelType
                                if (fuelType != null) {
                                    coroutineScope.launch {
                                        val latestFuelPrice = carViewModel.getLatestFuelPrice(fuelType)
                                        if (latestFuelPrice != null) {
                                            fuelCost = fuel * latestFuelPrice.pricePerUnit
                                        }
                                    }
                                }
                            }

                            coroutineScope.launch {
                                // Get fuel price data for the trip
                                var tripFuelPricePerUnit: Double? = null
                                var tripCurrencyId: String? = null
                                
                                if (isComplete) {
                                    val vehicle = selectedVehicle
                                    val fuelType = vehicle?.fuelType
                                    if (fuelType != null) {
                                        val latestFuelPrice = carViewModel.getLatestFuelPrice(fuelType)
                                        tripFuelPricePerUnit = latestFuelPrice?.pricePerUnit
                                        tripCurrencyId = latestFuelPrice?.currencyId
                                    }
                                }

                                val trip = Trip(
                                    id = currentTripId ?: generateUniqueTripId(),
                                    vehicleId = vehicle.id,
                                    vehicleName = vehicle.name,
                                    startMileage = start,
                                    endMileage = end,
                                    fuelFilled = fuel,
                                    tripDistance = if (isComplete) tripDistance else null,
                                    fuelEfficiency = if (isComplete) customCalculationResult else null,
                                    fuelCost = if (isComplete) fuelCost else null,
                                    fuelPricePerUnit = tripFuelPricePerUnit,
                                    currencyId = tripCurrencyId,
                                    status = if (isComplete) TripStatus.COMPLETED else TripStatus.DRAFT,
                                )

                                if (currentTripId != null || isTripInProgress) {
                                    carViewModel.updateTrip(trip, googleAccount)
                                } else {
                                    carViewModel.addTrip(trip, googleAccount)
                                }
                                isTripInProgress = true
                                currentTripId = trip.id
                                showMessage(
                                    if (isComplete) AppMessageType.SUCCESS else AppMessageType.WARNING,
                                    if (isComplete) "Trip saved successfully!" else "Trip Saved as Draft"
                                )
                            }
                        },
                        enabled = true,
                        contentPadding = ButtonDefaults.ContentPadding
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = "Save Trip")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Trip")
                    }
                }
            }
        }

        // Unified container will handle messages and results below

        if (isCalculating || showMessage || (tripDistance != null && customCalculationResult != null)) {
            item { Spacer(modifier = Modifier.height(10.dp)) }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = when (messageType) {
                        AppMessageType.ERROR -> CardDefaults.cardColors(
                            containerColor = Color(0xFF1C0E10),
                            contentColor = Color.White
                        )
                        AppMessageType.WARNING -> CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = Color.White
                        )
                        AppMessageType.SUCCESS -> CardDefaults.cardColors(
                            containerColor = Color(0xFF2F4578),
                            contentColor = Color.White
                        )
                        AppMessageType.INFO, null -> CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = Color.White
                        )
                    }
                ) {
                    // Header with optional status and close button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isCalculating) "Calculating..." else messageText.ifBlank { "Trip saved successfully!" },
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        IconButton(onClick = {
                            // Clear calculation/result container
                            isCalculating = false
                            tripDistance = null
                            customCalculationResult = null
                            fuelCost = null
                        }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Close Result")
                        }
                    }
                    HorizontalDivider(thickness = DividerDefaults.Thickness, color = Color.White.copy(alpha = 0.2f))

                    if (isCalculating) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            ContainedLoadingIndicator(modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Calculating...", color = Color.White)
                        }
                    } else if (tripDistance != null && customCalculationResult != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Trip Distance: ${decimalFormat.format(tripDistance)} km",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                            Text(
                                text = "Fuel Efficiency: ${decimalFormat.format(customCalculationResult)} km/${fuelUnit}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                            if (fuelCost != null) {
                                val currency = defaultCurrency
                                val currencySymbol = currency?.symbol ?: ""
                                Text(
                                    text = "Fuel Cost: $currencySymbol${decimalFormat.format(fuelCost)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


