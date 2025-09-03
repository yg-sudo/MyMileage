package com.yg.mileage.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getAllTripsForUser(userId: String): Flow<List<TripEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE id = :tripId AND userId = :userId")
    suspend fun getTripById(tripId: String, userId: String): TripEntity?

    @Query("DELETE FROM trips WHERE vehicleName = :vehicleName AND userId = :userId")
    suspend fun deleteTripsByVehicleName(vehicleName: String, userId: String)

    @Query("DELETE FROM trips WHERE userId = :userId")
    suspend fun deleteAllTripsForUser(userId: String)
}
