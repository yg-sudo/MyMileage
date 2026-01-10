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

import java.util.Date

data class Trip(
    val id: String = java.util.UUID.randomUUID().toString(),
    val vehicleId: String,
    val vehicleName: String,
    val startMileage: Double? = null,
    val endMileage: Double? = null,
    val fuelFilled: Double? = null,
    val tripDistance: Double? = null,
    val fuelEfficiency: Double? = null,
    val fuelCost: Double? = null, // Total fuel cost for this trip
    val fuelPricePerUnit: Double? = null, // Price per liter/kg at time of trip
    val currencyId: String? = null, // Currency used for this trip
    val tripGroup: String? = null, // Group ID if this trip belongs to a group
    val status: TripStatus = TripStatus.DRAFT,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class TripStatus {
    DRAFT,
    COMPLETED
} 