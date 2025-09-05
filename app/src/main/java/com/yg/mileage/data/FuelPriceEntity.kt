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
