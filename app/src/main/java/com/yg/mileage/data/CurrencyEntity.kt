/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2025  Yojit Ghadi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
