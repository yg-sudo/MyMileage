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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yg.mileage.Trip
import kotlinx.coroutines.flow.Flow

data class TripWithVehicle(
    @Embedded
    val trip: TripEntity,
    @Embedded(prefix = "v_")
    val vehicle: VehicleEntity
)

@Dao
interface TripDao {
    @Query(
        """
        SELECT 
            trips.*,
            vehicles.id AS v_id,
            vehicles.userId AS v_userId,
            vehicles.name AS v_name,
            vehicles.make AS v_make,
            vehicles.model AS v_model,
            vehicles.year AS v_year,
            vehicles.registrationNumber AS v_registrationNumber,
            vehicles.fuelType AS v_fuelType,
            vehicles.iconName AS v_iconName,
            vehicles.createdAt AS v_createdAt,
            vehicles.updatedAt AS v_updatedAt
        FROM trips 
        INNER JOIN vehicles ON trips.vehicleId = vehicles.id 
        WHERE trips.userId = :userId ORDER BY trips.updatedAt DESC
        """
    )
    fun getAllTripsForUser(userId: String): Flow<List<TripWithVehicle>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE id = :tripId AND userId = :userId")
    suspend fun getTripById(tripId: String, userId: String): TripEntity?

    @Query("DELETE FROM trips WHERE vehicleId = :vehicleId AND userId = :userId")
    suspend fun deleteTripsByVehicleId(vehicleId: String, userId: String)

    @Query("DELETE FROM trips WHERE userId = :userId")
    suspend fun deleteAllTripsForUser(userId: String)
}

fun List<TripWithVehicle>.toTripList(): List<Trip> {
    return this.map { (trip, vehicle) ->
        Trip(
            id = trip.id,
            vehicleId = trip.vehicleId,
            vehicleName = vehicle.name,
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
