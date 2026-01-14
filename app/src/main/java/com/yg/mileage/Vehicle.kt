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

data class Vehicle(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val fuelType: FuelType? = null,
    val registrationNumber: String = ""
)

enum class FuelType(val displayName: String) {
    PETROL("Petrol"),
    DIESEL("Diesel"),
    CNG("CNG")
}
