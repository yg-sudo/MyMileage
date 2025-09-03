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
                vehicleName = trip.vehicleName,
                startMileage = trip.startMileage,
                endMileage = trip.endMileage,
                fuelFilled = trip.fuelFilled,
                tripDistance = trip.tripDistance,
                fuelEfficiency = trip.fuelEfficiency,
                status = trip.status,
                createdAt = trip.createdAt,
                updatedAt = trip.updatedAt
            )
        }
    }
}