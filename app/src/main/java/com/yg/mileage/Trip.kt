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
    val status: TripStatus = TripStatus.DRAFT,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class TripStatus {
    DRAFT,
    COMPLETED
} 