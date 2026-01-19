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

import androidx.compose.material.icons.Icons.Rounded
import androidx.compose.material.icons.rounded.Agriculture
import androidx.compose.material.icons.rounded.AirportShuttle
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.ElectricCar
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.LocalTaxi
import androidx.compose.material.icons.rounded.Moped
import androidx.compose.material.icons.rounded.NoCrash
import androidx.compose.material.icons.rounded.PedalBike
import androidx.compose.material.icons.rounded.RvHookup
import androidx.compose.material.icons.rounded.TwoWheeler
import androidx.compose.ui.graphics.vector.ImageVector

data class Vehicle(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val fuelType: FuelType? = null,
    val registrationNumber: String = "",
    val iconName: String = "DirectionsCar" // Default icon name
)

enum class FuelType(val displayName: String) {
    PETROL("Petrol"),
    DIESEL("Diesel"),
    CNG("CNG")
}

val VehicleIcons = listOf(
    "DirectionsCar" to Rounded.DirectionsCar,
    "LocalShipping" to Rounded.LocalShipping,
    "AirportShuttle" to Rounded.AirportShuttle,
    "TwoWheeler" to Rounded.TwoWheeler,
    "PedalBike" to Rounded.PedalBike,
    "ElectricCar" to Rounded.ElectricCar,
    "LocalTaxi" to Rounded.LocalTaxi,
    "Agriculture" to Rounded.Agriculture,
    "NoCrash" to Rounded.NoCrash,
    "Moped" to Rounded.Moped,
    "RvHookup" to Rounded.RvHookup
)

fun getVehicleIcon(name: String): ImageVector {
    return VehicleIcons.find { it.first == name }?.second ?: Rounded.DirectionsCar
}
