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

package com.yg.mileage.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yg.mileage.FuelType
import com.yg.mileage.Vehicle
import java.util.Date

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val id: String,
    val userId: String, // <<< NEW FIELD
    val name: String,
    val fuelType: FuelType?,
    val createdAt: Date,
    val updatedAt: Date
) {
    fun toVehicle(): Vehicle {
        return Vehicle(id = id, name = name, fuelType = fuelType)
    }

    companion object {
        fun fromVehicle(vehicle: Vehicle, userId: String): VehicleEntity {
            return VehicleEntity(
                id = vehicle.id,
                userId = userId,
                name = vehicle.name,
                fuelType = vehicle.fuelType,
                createdAt = Date(),
                updatedAt = Date()
            )
        }
    }
}
