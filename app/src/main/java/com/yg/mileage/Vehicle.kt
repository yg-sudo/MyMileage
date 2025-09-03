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