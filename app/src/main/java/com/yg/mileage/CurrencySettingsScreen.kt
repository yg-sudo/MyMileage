@file:OptIn(ExperimentalMaterial3Api::class)

package com.yg.mileage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.DecimalFormat
import kotlinx.coroutines.launch

@Composable
fun CurrencySettingsScreen(
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel = viewModel()
) {
    val currencies by carViewModel.currencies.collectAsState()
    val fuelPrices by carViewModel.fuelPrices.collectAsState()
    val defaultCurrency by carViewModel.defaultCurrency.collectAsState()
    var showAddCurrencyDialog by remember { mutableStateOf(false) }
    var showAddFuelPriceDialog by remember { mutableStateOf(false) }
    var editingCurrency by remember { mutableStateOf<Currency?>(null) }
    var editingFuelPrice by remember { mutableStateOf<FuelPrice?>(null) }
    val coroutineScope = rememberCoroutineScope() // Add this line

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Currency & Fuel Prices",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Default Currency Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Default Currency",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (defaultCurrency != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${defaultCurrency!!.symbol} ${defaultCurrency!!.name} (${defaultCurrency!!.code})",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            IconButton(onClick = { editingCurrency = defaultCurrency }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Currency")
                            }
                        }
                    } else {
                        Text(
                            text = "No default currency set",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Currencies Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Currencies",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { showAddCurrencyDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Currency")
                }
            }
        }

        items(currencies) { currency ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${currency.symbol} ${currency.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = currency.code,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (currency.isDefault) {
                            Text(
                                text = "Default",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Row {
                        if (!currency.isDefault) {
                            IconButton(onClick = {
                                coroutineScope.launch { carViewModel.setDefaultCurrency(currency.id) }
                            }) {
                                Text(
                                    text = "Set Default",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(onClick = { editingCurrency = currency }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = {
                            coroutineScope.launch { carViewModel.deleteCurrency(currency) }
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }

        // Fuel Prices Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fuel Prices",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = { showAddFuelPriceDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Fuel Price")
                }
            }
        }

        items(fuelPrices) { fuelPrice ->
            val currency = currencies.find { it.id == fuelPrice.currencyId }
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = fuelPrice.fuelType.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${currency?.symbol ?: ""}${DecimalFormat("#,##0.00").format(fuelPrice.pricePerUnit)} per ${if (fuelPrice.fuelType == FuelType.CNG) "KG" else "Ltr"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Updated: ${java.text.SimpleDateFormat("MMM dd, yyyy").format(fuelPrice.lastUpdated)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row {
                        IconButton(onClick = { editingFuelPrice = fuelPrice }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = {
                            coroutineScope.launch { carViewModel.deleteFuelPrice(fuelPrice) }
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }

    // Add/Edit Currency Dialog
    if (showAddCurrencyDialog || editingCurrency != null) {
        CurrencyDialog(
            currency = editingCurrency,
            onDismiss = {
                showAddCurrencyDialog = false
                editingCurrency = null
            },
            onSave = { currency ->
                coroutineScope.launch {
                    if (editingCurrency != null) {
                        carViewModel.updateCurrency(currency)
                    } else {
                        carViewModel.addCurrency(currency)
                    }
                    showAddCurrencyDialog = false
                    editingCurrency = null
                }
            }
        )
    }

    // Add/Edit Fuel Price Dialog
    if (showAddFuelPriceDialog || editingFuelPrice != null) {
        FuelPriceDialog(
            fuelPrice = editingFuelPrice,
            currencies = currencies,
            onDismiss = {
                showAddFuelPriceDialog = false
                editingFuelPrice = null
            },
            onSave = { fuelPrice ->
                coroutineScope.launch {
                    if (editingFuelPrice != null) {
                        carViewModel.updateFuelPrice(fuelPrice)
                    } else {
                        carViewModel.addFuelPrice(fuelPrice)
                    }
                    showAddFuelPriceDialog = false
                    editingFuelPrice = null
                }
            }
        )
    }
}


@Composable
fun CurrencyDialog(
    currency: Currency?,
    onDismiss: () -> Unit,
    onSave: (Currency) -> Unit
) {
    var code by remember { mutableStateOf(currency?.code ?: "") }
    var name by remember { mutableStateOf(currency?.name ?: "") }
    var symbol by remember { mutableStateOf(currency?.symbol ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (currency != null) "Edit Currency" else "Add Currency") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = { Text("Currency Code") },
                    placeholder = { Text("e.g., USD, EUR, INR") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Currency Name") },
                    placeholder = { Text("e.g., US Dollar, Euro") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = { Text("Currency Symbol") },
                    placeholder = { Text("e.g., $, €, ₹") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (code.isNotBlank() && name.isNotBlank() && symbol.isNotBlank()) {
                        onSave(Currency(
                            id = currency?.id ?: java.util.UUID.randomUUID().toString(),
                            code = code,
                            name = name,
                            symbol = symbol,
                            isDefault = currency?.isDefault ?: false
                        ))
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FuelPriceDialog(
    fuelPrice: FuelPrice?,
    currencies: List<Currency>,
    onDismiss: () -> Unit,
    onSave: (FuelPrice) -> Unit
) {
    var selectedFuelType by remember { mutableStateOf(fuelPrice?.fuelType ?: FuelType.PETROL) }
    var priceText by remember { mutableStateOf(fuelPrice?.pricePerUnit?.toString() ?: "") }
    var selectedCurrencyId by remember { mutableStateOf(fuelPrice?.currencyId ?: currencies.firstOrNull()?.id ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (fuelPrice != null) "Edit Fuel Price" else "Add Fuel Price") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Fuel Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { }
                ) {
                    OutlinedTextField(
                        value = selectedFuelType.displayName,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Fuel Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                }

                // Currency Dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { }
                ) {
                    val selectedCurrency = currencies.find { it.id == selectedCurrencyId }
                    OutlinedTextField(
                        value = selectedCurrency?.let { "${it.symbol} ${it.name} (${it.code})" } ?: "Select Currency",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Currency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { 
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                            priceText = it
                        }
                    },
                    label = { Text("Price per ${if (selectedFuelType == FuelType.CNG) "KG" else "Ltr"}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val price = priceText.toDoubleOrNull()
                    if (price != null && price > 0 && selectedCurrencyId.isNotBlank()) {
                        onSave(FuelPrice(
                            id = fuelPrice?.id ?: java.util.UUID.randomUUID().toString(),
                            fuelType = selectedFuelType,
                            pricePerUnit = price,
                            currencyId = selectedCurrencyId,
                            lastUpdated = java.util.Date(),
                            isActive = true
                        ))
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
