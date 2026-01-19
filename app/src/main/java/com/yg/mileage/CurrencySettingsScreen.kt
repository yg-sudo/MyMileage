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

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.yg.mileage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yg.mileage.ui.theme.robotoFlexTopAppBar
import kotlinx.coroutines.launch
import java.text.DateFormat.getDateInstance
import java.text.DecimalFormat
import java.util.Date
import java.util.UUID

@Composable
fun CurrencySettingsScreen(
    modifier: Modifier = Modifier,
    carViewModel: CarViewModel = viewModel()
) {
    val currencies by carViewModel.currencies.collectAsState()
    val fuelPrices by carViewModel.fuelPrices.collectAsState()
    val defaultCurrency by carViewModel.defaultCurrency.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    CurrencySettingsScreenContent(
        modifier = modifier,
        currencies = currencies,
        fuelPrices = fuelPrices,
        defaultCurrency = defaultCurrency,
        onAddCurrency = { currency -> coroutineScope.launch { carViewModel.addCurrency(currency) } },
        onUpdateCurrency = { currency -> coroutineScope.launch { carViewModel.updateCurrency(currency) } },
        onDeleteCurrency = { currency -> coroutineScope.launch { carViewModel.deleteCurrency(currency) } },
        onSetDefaultCurrency = { currencyId -> coroutineScope.launch { carViewModel.setDefaultCurrency(currencyId) } },
        onAddFuelPrice = { fuelPrice -> coroutineScope.launch { carViewModel.addFuelPrice(fuelPrice) } },
        onUpdateFuelPrice = { fuelPrice -> coroutineScope.launch { carViewModel.updateFuelPrice(fuelPrice) } },
        onDeleteFuelPrice = { fuelPrice -> coroutineScope.launch { carViewModel.deleteFuelPrice(fuelPrice) } }
    )
}

@Composable
fun CurrencySettingsScreenContent(
    modifier: Modifier = Modifier,
    currencies: List<Currency>,
    fuelPrices: List<FuelPrice>,
    defaultCurrency: Currency?,
    onAddCurrency: (Currency) -> Unit,
    onUpdateCurrency: (Currency) -> Unit,
    onDeleteCurrency: (Currency) -> Unit,
    onSetDefaultCurrency: (String) -> Unit,
    onAddFuelPrice: (FuelPrice) -> Unit,
    onUpdateFuelPrice: (FuelPrice) -> Unit,
    onDeleteFuelPrice: (FuelPrice) -> Unit
) {
    var showAddCurrencyDialog by remember { mutableStateOf(false) }
    var showAddFuelPriceDialog by remember { mutableStateOf(false) }
    var editingCurrency by remember { mutableStateOf<Currency?>(null) }
    var editingFuelPrice by remember { mutableStateOf<FuelPrice?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        fontFamily = robotoFlexTopAppBar,
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
                                text = "${defaultCurrency.symbol} ${defaultCurrency.name} (${defaultCurrency.code})",
                                fontFamily = robotoFlexTopAppBar
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
                            IconButton(onClick = { onSetDefaultCurrency(currency.id) }) {
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
                        IconButton(onClick = { onDeleteCurrency(currency) }) {
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
                            text = "Updated: ${getDateInstance(1).format(fuelPrice.lastUpdated).format(fuelPrice.lastUpdated)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row {
                        IconButton(onClick = { editingFuelPrice = fuelPrice }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { onDeleteFuelPrice(fuelPrice) }) {
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
                if (editingCurrency != null) {
                    onUpdateCurrency(currency)
                } else {
                    onAddCurrency(currency)
                }
                showAddCurrencyDialog = false
                editingCurrency = null
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
                if (editingFuelPrice != null) {
                    onUpdateFuelPrice(fuelPrice)
                } else {
                    onAddFuelPrice(fuelPrice)
                }
                showAddFuelPriceDialog = false
                editingFuelPrice = null
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencySettingsScreenPreview() {
    val sampleCurrencies = listOf(
        Currency(id = "1", code = "USD", name = "US Dollar", symbol = "$", isDefault = true),
        Currency(id = "2", code = "EUR", name = "Euro", symbol = "€"),
        Currency(id = "3", code = "INR", name = "Indian Rupee", symbol = "₹")
    )
    val sampleFuelPrices = listOf(
        FuelPrice(id = "1", fuelType = FuelType.PETROL, pricePerUnit = 1.50, currencyId = "1", lastUpdated = Date(), isActive = true),
        FuelPrice(id = "2", fuelType = FuelType.DIESEL, pricePerUnit = 1.30, currencyId = "1", lastUpdated = Date(), isActive = true),
        FuelPrice(id = "3", fuelType = FuelType.CNG, pricePerUnit = 0.90, currencyId = "1", lastUpdated = Date(), isActive = true)
    )
    CurrencySettingsScreenContent(
        currencies = sampleCurrencies,
        fuelPrices = sampleFuelPrices,
        defaultCurrency = sampleCurrencies.first { it.isDefault },
        onAddCurrency = {},
        onUpdateCurrency = {},
        onDeleteCurrency = {},
        onSetDefaultCurrency = {},
        onAddFuelPrice = {},
        onUpdateFuelPrice = {},
        onDeleteFuelPrice = {}
    )
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
                        onSave(
                            Currency(
                                id = currency?.id ?: UUID.randomUUID().toString(),
                                code = code,
                                name = name,
                                symbol = symbol,
                                isDefault = currency?.isDefault ?: false
                            )
                        )
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

@Preview
@Composable
fun AddCurrencyDialogPreview() {
    CurrencyDialog(
        currency = null,
        onDismiss = {},
        onSave = {}
    )
}

@Preview
@Composable
fun EditCurrencyDialogPreview() {
    val sampleCurrency = Currency(code = "USD", name = "US Dollar", symbol = "$", isDefault = true)
    CurrencyDialog(
        currency = sampleCurrency,
        onDismiss = {},
        onSave = {}
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
    var selectedCurrencyId by remember { mutableStateOf(fuelPrice?.currencyId ?: currencies.firstOrNull { it.isDefault }?.id ?: currencies.firstOrNull()?.id ?: "") }
    var isActive by remember { mutableStateOf(fuelPrice?.isActive ?: true) } // State for the checkbox
    var isFuelTypeMenuExpanded by remember { mutableStateOf(false) }
    var isCurrencyMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (fuelPrice != null) "Edit Fuel Price" else "Add Fuel Price") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Fuel Type Dropdown (Existing code)
                ExposedDropdownMenuBox(
                    expanded = isFuelTypeMenuExpanded,
                    onExpandedChange = { isFuelTypeMenuExpanded = !isFuelTypeMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedFuelType.displayName,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Fuel Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFuelTypeMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = isFuelTypeMenuExpanded,
                        onDismissRequest = { isFuelTypeMenuExpanded = false }
                    ) {
                        FuelType.entries.forEach { fuelType ->
                            DropdownMenuItem(
                                text = { Text(fuelType.displayName) },
                                onClick = {
                                    selectedFuelType = fuelType
                                    isFuelTypeMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Currency Dropdown (Existing code)
                ExposedDropdownMenuBox(
                    expanded = isCurrencyMenuExpanded,
                    onExpandedChange = { isCurrencyMenuExpanded = !isCurrencyMenuExpanded }
                ) {
                    val selectedCurrency = currencies.find { it.id == selectedCurrencyId }
                    OutlinedTextField(
                        value = selectedCurrency?.let { "${it.symbol} ${it.name} (${it.code})" } ?: "Select Currency",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Currency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCurrencyMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isCurrencyMenuExpanded,
                        onDismissRequest = { isCurrencyMenuExpanded = false }
                    ) {
                        currencies.forEach { currency ->
                            DropdownMenuItem(
                                text = { Text("${currency.symbol} ${currency.name} (${currency.code})") },
                                onClick = {
                                    selectedCurrencyId = currency.id
                                    isCurrencyMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = priceText,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            priceText = it
                        }
                    },
                    label = { Text("Price per ${if (selectedFuelType == FuelType.CNG) "KG" else "Ltr"}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Added "Active" Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                    Text(
                        text = "Active Price",
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val price = priceText.toDoubleOrNull()
                    if (price != null && price > 0 && selectedCurrencyId.isNotBlank()) {
                        onSave(
                            FuelPrice(
                                id = fuelPrice?.id ?: UUID.randomUUID().toString(),
                                fuelType = selectedFuelType,
                                pricePerUnit = price,
                                currencyId = selectedCurrencyId,
                                lastUpdated = Date(),
                                isActive = isActive // Pass the checkbox state to the model
                            )
                        )
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

@Preview
@Composable
fun AddFuelPriceDialogPreview() {
    val sampleCurrencies = listOf(
        Currency(id = "1", code = "USD", name = "US Dollar", symbol = "$"),
        Currency(id = "2", code = "EUR", name = "Euro", symbol = "€")
    )
    FuelPriceDialog(
        fuelPrice = null,
        currencies = sampleCurrencies,
        onDismiss = {},
        onSave = {}
    )
}

@Preview
@Composable
fun EditFuelPriceDialogPreview() {
    val sampleCurrencies = listOf(
        Currency(id = "1", code = "USD", name = "US Dollar", symbol = "$", isDefault = true),
        Currency(id = "2", code = "EUR", name = "Euro", symbol = "€")
    )
    val sampleFuelPrice = FuelPrice(fuelType = FuelType.PETROL, pricePerUnit = 1.50, currencyId = "1", lastUpdated = Date())
    FuelPriceDialog(
        fuelPrice = sampleFuelPrice,
        currencies = sampleCurrencies,
        onDismiss = {},
        onSave = {}
    )
}
