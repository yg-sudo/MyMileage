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
import com.yg.mileage.Trip
import com.yg.mileage.TripStatus
import java.util.Date

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val userId: String, // <<< NEW FIELD
    val vehicleId: String,
    val vehicleName: String,
    val startMileage: Double?,
    val endMileage: Double?,
    val fuelFilled: Double?,
    val tripDistance: Double?,
    val fuelEfficiency: Double?,
    val fuelCost: Double?,
    val fuelPricePerUnit: Double?,
    val currencyId: String?,
    val tripGroup: String?,
    val status: TripStatus,
    val createdAt: Date,
    val updatedAt: Date
) {
    fun toTrip(): Trip {
        return Trip(
            id = id,
            vehicleId = vehicleId,
            vehicleName = vehicleName,
            startMileage = startMileage,
            endMileage = endMileage,
            fuelFilled = fuelFilled,
            tripDistance = tripDistance,
            fuelEfficiency = fuelEfficiency,
            fuelCost = fuelCost,
            fuelPricePerUnit = fuelPricePerUnit,
            currencyId = currencyId,
            tripGroup = tripGroup,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromTrip(trip: Trip, userId: String): TripEntity {
            return TripEntity(
                id = trip.id,
                userId = userId, // <<< attach userId
                vehicleId = trip.vehicleId,
                vehicleName = "",
                startMileage = trip.startMileage,
                endMileage = trip.endMileage,
                fuelFilled = trip.fuelFilled,
                tripDistance = trip.tripDistance,
                fuelEfficiency = trip.fuelEfficiency,
                fuelCost = trip.fuelCost,
                fuelPricePerUnit = trip.fuelPricePerUnit,
                currencyId = trip.currencyId,
                tripGroup = trip.tripGroup,
                status = trip.status,
                createdAt = trip.createdAt,
                updatedAt = trip.updatedAt
            )
        }
    }
}