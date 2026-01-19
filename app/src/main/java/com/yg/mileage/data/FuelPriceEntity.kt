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

package com.yg.mileage.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yg.mileage.FuelPrice
import com.yg.mileage.FuelType
import java.util.Date

@Entity(tableName = "fuel_prices")
data class FuelPriceEntity(
    @PrimaryKey val id: String,
    val fuelType: FuelType,
    val pricePerUnit: Double,
    val currencyId: String,
    val lastUpdated: Date,
    val isActive: Boolean = true
) {
    fun toFuelPrice(): FuelPrice {
        return FuelPrice(
            id = id,
            fuelType = fuelType,
            pricePerUnit = pricePerUnit,
            currencyId = currencyId,
            lastUpdated = lastUpdated,
            isActive = isActive
        )
    }

    companion object {
        fun fromFuelPrice(fuelPrice: FuelPrice): FuelPriceEntity {
            return FuelPriceEntity(
                id = fuelPrice.id,
                fuelType = fuelPrice.fuelType,
                pricePerUnit = fuelPrice.pricePerUnit,
                currencyId = fuelPrice.currencyId,
                lastUpdated = fuelPrice.lastUpdated,
                isActive = fuelPrice.isActive
            )
        }
    }
}
