package com.yg.mileage.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yg.mileage.Currency

@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey val id: String,
    val code: String,
    val name: String,
    val symbol: String,
    val isDefault: Boolean = false
) {
    fun toCurrency(): Currency {
        return Currency(
            id = id,
            code = code,
            name = name,
            symbol = symbol,
            isDefault = isDefault
        )
    }

    companion object {
        fun fromCurrency(currency: Currency): CurrencyEntity {
            return CurrencyEntity(
                id = currency.id,
                code = currency.code,
                name = currency.name,
                symbol = currency.symbol,
                isDefault = currency.isDefault
            )
        }
    }
}
