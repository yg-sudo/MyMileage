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
