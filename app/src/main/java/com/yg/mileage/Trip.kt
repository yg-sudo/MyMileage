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
    val status: TripStatus = TripStatus.DRAFT,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class TripStatus {
    DRAFT,
    COMPLETED
} 