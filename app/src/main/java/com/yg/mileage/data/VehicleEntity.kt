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