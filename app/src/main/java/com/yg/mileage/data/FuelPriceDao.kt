package com.yg.mileage.data

import androidx.room.*
import com.yg.mileage.FuelType
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelPriceDao {
    @Query("SELECT * FROM fuel_prices WHERE isActive = 1 ORDER BY lastUpdated DESC")
    fun getAllActiveFuelPrices(): Flow<List<FuelPriceEntity>>

    @Query("SELECT * FROM fuel_prices WHERE fuelType = :fuelType AND isActive = 1 ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestFuelPrice(fuelType: FuelType): FuelPriceEntity?

    @Query("SELECT * FROM fuel_prices WHERE id = :id")
    suspend fun getFuelPriceById(id: String): FuelPriceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelPrice(fuelPrice: FuelPriceEntity)

    @Update
    suspend fun updateFuelPrice(fuelPrice: FuelPriceEntity)

    @Delete
    suspend fun deleteFuelPrice(fuelPrice: FuelPriceEntity)

    @Query("UPDATE fuel_prices SET isActive = 0 WHERE fuelType = :fuelType")
    suspend fun deactivateFuelPrices(fuelType: FuelType)

    @Query("SELECT * FROM fuel_prices WHERE fuelType = :fuelType AND currencyId = :currencyId AND isActive = 1 ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestFuelPriceByCurrency(fuelType: FuelType, currencyId: String): FuelPriceEntity?
}
