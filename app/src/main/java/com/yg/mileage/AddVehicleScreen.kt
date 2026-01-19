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

package com.yg.mileage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddVehicleScreen(
    navController: NavController,
    onSaveVehicle: (Vehicle) -> Unit,
    vehicleToEdit: Vehicle? = null
) {
    var vehicleName by remember {
        mutableStateOf(TextFieldValue(vehicleToEdit?.name ?: ""))
    }
    var make by remember {
        mutableStateOf(TextFieldValue(vehicleToEdit?.make ?: ""))
    }
    var model by remember {
        mutableStateOf(TextFieldValue(vehicleToEdit?.model ?: ""))
    }
    var year by remember {
        mutableStateOf(TextFieldValue(vehicleToEdit?.year ?: ""))
    }
    var fuelType by remember {
        mutableStateOf(vehicleToEdit?.fuelType)
    }
    var registrationNumber by remember {
        mutableStateOf(TextFieldValue(vehicleToEdit?.registrationNumber ?: ""))
    }
    var selectedIconName by remember {
        mutableStateOf(vehicleToEdit?.iconName ?: "DirectionsCar")
    }
    
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var fuelTypeError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Vehicle Name TextField (Required)
        OutlinedTextField(
            value = vehicleName,
            onValueChange = {
                vehicleName = it
                isError = false
                errorMessage = ""
            },
            label = { Text("Vehicle Name *") },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            supportingText = {
                if (isError) {
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Vehicle Icon Dropdown
        var iconDropdownExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = iconDropdownExpanded,
            onExpandedChange = { iconDropdownExpanded = !iconDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedIconName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Vehicle Icon") },
                leadingIcon = {
                    Icon(
                        imageVector = getVehicleIcon(selectedIconName),
                        contentDescription = null
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = iconDropdownExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
            )
            ExposedDropdownMenu(
                expanded = iconDropdownExpanded,
                onDismissRequest = { iconDropdownExpanded = false }
            ) {
                VehicleIcons.forEach { (name, icon) ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(name)
                            }
                        },
                        onClick = {
                            selectedIconName = name
                            iconDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Make TextField (Optional)
        OutlinedTextField(
            value = make,
            onValueChange = { make = it },
            label = { Text("Make") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Model TextField (Optional)
        OutlinedTextField(
            value = model,
            onValueChange = { model = it },
            label = { Text("Model") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Year TextField (Optional)
        OutlinedTextField(
            value = year,
            onValueChange = { year = it },
            label = { Text("Year") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fuel Type Dropdown (Required)
        var fuelTypeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = fuelTypeExpanded,
            onExpandedChange = { fuelTypeExpanded = !fuelTypeExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = fuelType?.displayName ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Fuel Type *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelTypeExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true),
                isError = fuelTypeError,
                supportingText = {
                    if (fuelTypeError) {
                        Text("Please select a fuel type", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            ExposedDropdownMenu(
                expanded = fuelTypeExpanded,
                onDismissRequest = { fuelTypeExpanded = false }
            ) {
                FuelType.entries.forEach { fuel ->
                    DropdownMenuItem(
                        text = { Text(fuel.displayName) },
                        onClick = {
                            fuelType = fuel
                            fuelTypeError = false
                            fuelTypeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Registration Number TextField (Optional)
        OutlinedTextField(
            value = registrationNumber,
            onValueChange = { registrationNumber = it },
            label = { Text("Registration Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    val name = vehicleName.text.trim()
                    when {
                        name.isEmpty() -> {
                            isError = true
                            errorMessage = "Vehicle name cannot be empty"
                        }
                        name.length < 2 -> {
                            isError = true
                            errorMessage = "Vehicle name must be at least 2 characters"
                        }
                        name.length > 50 -> {
                            isError = true
                            errorMessage = "Vehicle name cannot exceed 50 characters"
                        }
                        fuelType == null -> {
                            fuelTypeError = true
                        }
                        else -> {
                            val newVehicle = vehicleToEdit?.copy(
                                name = name,
                                make = make.text.trim(),
                                model = model.text.trim(),
                                year = year.text.trim(),
                                fuelType = fuelType,
                                registrationNumber = registrationNumber.text.trim(),
                                iconName = selectedIconName
                            ) ?: Vehicle(
                                name = name,
                                make = make.text.trim(),
                                model = model.text.trim(),
                                year = year.text.trim(),
                                fuelType = fuelType,
                                registrationNumber = registrationNumber.text.trim(),
                                iconName = selectedIconName
                            )
                            onSaveVehicle(newVehicle)
                            navController.navigateUp()
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (vehicleToEdit != null) "Update" else "Save")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun AddVehicleScreenPreview() {
    AddVehicleScreen(
        navController = rememberNavController(),
        onSaveVehicle = {}
    )
}
