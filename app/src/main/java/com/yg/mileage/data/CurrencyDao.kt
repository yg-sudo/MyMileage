package com.yg.mileage.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies ORDER BY isDefault DESC, name ASC")
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultCurrency(): CurrencyEntity?

    @Query("SELECT * FROM currencies WHERE id = :id")
    suspend fun getCurrencyById(id: String): CurrencyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: CurrencyEntity)

    @Update
    suspend fun updateCurrency(currency: CurrencyEntity)

    @Delete
    suspend fun deleteCurrency(currency: CurrencyEntity)

    @Query("UPDATE currencies SET isDefault = 0")
    suspend fun clearDefaultCurrencies()

    @Query("UPDATE currencies SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultCurrency(id: String)
}
