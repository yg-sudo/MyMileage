package com.yg.mileage.data

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.yg.mileage.Trip
import com.yg.mileage.Vehicle
import com.yg.mileage.auth.DriveService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class Repository(
    private val database: AppDatabase,
    private val driveService: DriveService
) {
    private val vehicleDao = database.vehicleDao()
    private val tripDao = database.tripDao()

    // --- VEHICLE ---
    fun getAllVehicles(userId: String): Flow<List<Vehicle>> =
        vehicleDao.getAllVehiclesForUser(userId).map { it.map { e -> e.toVehicle() } }

    suspend fun addVehicle(vehicle: Vehicle, userId: String) {
        vehicleDao.insertVehicle(VehicleEntity.fromVehicle(vehicle, userId))
    }

    suspend fun updateVehicle(vehicle: Vehicle, userId: String) {
        vehicleDao.updateVehicle(VehicleEntity.fromVehicle(vehicle, userId))
    }

    suspend fun deleteVehicle(vehicleName: String, userId: String) {
        val v = vehicleDao.getVehicleByName(vehicleName, userId)
        v?.let { vehicleDao.deleteVehicle(it) }
    }

    suspend fun canDeleteVehicle(vehicleName: String, userId: String): Boolean {
        val vehicle = vehicleDao.getVehicleByName(vehicleName, userId)
        return vehicle?.let { vehicleDao.getTripCountForVehicle(it.name, userId) == 0 } ?: false
    }

    // --- TRIPS ---
    fun getAllTrips(userId: String): Flow<List<Trip>> =
        tripDao.getAllTripsForUser(userId).map { it.map { e -> e.toTrip() } }

    suspend fun addTrip(trip: Trip, userId: String) {
        val tripEntity = TripEntity.fromTrip(trip, userId)
        Log.d("Repository", "Attempting to insert tripEntity into DAO: $tripEntity")
        try {
            tripDao.insertTrip(tripEntity)
            Log.d("Repository", "tripDao.insertTrip called successfully for id: ${tripEntity.id}")
        } catch (e: Exception) {
            Log.e("Repository", "Error inserting tripEntity into DAO for id: ${tripEntity.id}", e)
            throw e // Re-throw to be caught by ViewModel if it can
        }
    }

    suspend fun updateTrip(trip: Trip, userId: String) {
        val tripEntity = TripEntity.fromTrip(trip, userId)
        Log.d("Repository", "Attempting to update tripEntity in DAO: $tripEntity")
        try {
            tripDao.updateTrip(tripEntity)
            Log.d("Repository", "tripDao.updateTrip called successfully for id: ${tripEntity.id}")
        } catch (e: Exception) {
            Log.e("Repository", "Error updating tripEntity in DAO for id: ${tripEntity.id}", e)
            throw e // Re-throw to be caught by ViewModel if it can
        }
    }

    suspend fun deleteTrip(tripId: String, userId: String) {
        val trip = tripDao.getTripById(tripId, userId)
        trip?.let { tripDao.deleteTrip(it) }
    }

    // --- Optional: Google Drive Backup (Only for Google Users) ---
    suspend fun backupTripsToDrive(userId: String, googleAccount: GoogleSignInAccount): Boolean {
        // You should implement DriveService.saveTripsToDrive for this
        return try {
            val allTrips = getAllTrips(userId).first()
            driveService.saveTripsToDrive(googleAccount, allTrips)
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        fun getRepository(context: Context): Repository {
            val database = AppDatabase.getDatabase(context)
            val driveService = DriveService(context)
            return Repository(database, driveService)
        }
    }
}

