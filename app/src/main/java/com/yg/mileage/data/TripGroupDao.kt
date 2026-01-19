/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2026 Yojit Ghadi
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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripGroupDao {
    @Insert
    suspend fun insertTripGroup(tripGroup: TripGroupEntity)

    @Update
    suspend fun updateTripGroup(tripGroup: TripGroupEntity)

    @Delete
    suspend fun deleteTripGroup(tripGroup: TripGroupEntity)

    @Query("SELECT * FROM tripGroup WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllTripGroups(userId: String): Flow<List<TripGroupEntity>>

    @Query("SELECT * FROM tripGroup WHERE id = :groupId AND userId = :userId")
    suspend fun getTripGroupById(groupId: String, userId: String): TripGroupEntity?
}
