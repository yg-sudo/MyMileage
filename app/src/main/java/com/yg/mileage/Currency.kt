/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2026 Yojit Ghadi
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

data class Currency(
    val id: String = java.util.UUID.randomUUID().toString(),
    val code: String, // e.g., "USD", "EUR", "INR"
    val name: String, // e.g., "US Dollar", "Euro", "Indian Rupee"
    val symbol: String, // e.g., "$", "€", "₹"
    val isDefault: Boolean = false
)

data class FuelPrice(
    val id: String = java.util.UUID.randomUUID().toString(),
    val fuelType: FuelType,
    val pricePerUnit: Double, // Price per liter/kg
    val currencyId: String,
    val lastUpdated: java.util.Date = java.util.Date(),
    val isActive: Boolean = true
)

data class CurrencySettings(
    val defaultCurrency: Currency,
    val fuelPrices: Map<FuelType, FuelPrice>
)
