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

import android.content.Context
import android.util.Log
import com.yg.mileage.Currency
import com.yg.mileage.FuelPrice
import com.yg.mileage.FuelType
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
    private val tripGroupDao = database.tripGroupDao()
    private val currencyDao = database.currencyDao()
    private val fuelPriceDao = database.fuelPriceDao()

    // --- VEHICLE ---
    fun getAllVehicles(userId: String): Flow<List<Vehicle>> =
        vehicleDao.getAllVehiclesForUser(userId).map { it.map { e -> e.toVehicle() } }

    suspend fun addVehicle(vehicle: Vehicle, userId: String) {
        vehicleDao.insertVehicle(VehicleEntity.fromVehicle(vehicle, userId))
    }

    suspend fun updateVehicle(vehicle: Vehicle, userId: String) {
        vehicleDao.updateVehicle(VehicleEntity.fromVehicle(vehicle, userId))
    }

    suspend fun deleteVehicle(vehicleId: String, userId: String) {
        val v = vehicleDao.getVehicleById(vehicleId, userId)
        v?.let { vehicleDao.deleteVehicle(it) }
    }

    suspend fun canDeleteVehicle(vehicleId: String, userId: String): Boolean {
        return !vehicleDao.hasTrips(vehicleId, userId)
    }

    // --- TRIPS ---
    fun getAllTrips(userId: String): Flow<List<Trip>> =
        tripDao.getAllTripsForUser(userId).map { it.toTripList() }

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

    // --- TRIP GROUP ---
    suspend fun saveTripGroup(tripGroup: TripGroupEntity) {
        tripGroupDao.insertTripGroup(tripGroup)
    }

    // --- CURRENCY ---
    fun getAllCurrencies(): Flow<List<Currency>> =
        currencyDao.getAllCurrencies().map { it.map { e -> e.toCurrency() } }

    suspend fun getDefaultCurrency(): Currency? =
        currencyDao.getDefaultCurrency()?.toCurrency()

    suspend fun addCurrency(currency: Currency) {
        currencyDao.insertCurrency(CurrencyEntity.fromCurrency(currency))
    }

    suspend fun updateCurrency(currency: Currency) {
        currencyDao.updateCurrency(CurrencyEntity.fromCurrency(currency))
    }

    suspend fun deleteCurrency(currency: Currency) {
        currencyDao.deleteCurrency(CurrencyEntity.fromCurrency(currency))
    }

    suspend fun setDefaultCurrency(currencyId: String) {
        currencyDao.clearDefaultCurrencies()
        currencyDao.setDefaultCurrency(currencyId)
    }

    // --- FUEL PRICES ---
    fun getAllActiveFuelPrices(): Flow<List<FuelPrice>> =
        fuelPriceDao.getAllActiveFuelPrices().map { it.map { e -> e.toFuelPrice() } }

    suspend fun getLatestFuelPrice(fuelType: FuelType): FuelPrice? =
        fuelPriceDao.getLatestFuelPrice(fuelType)?.toFuelPrice()

    suspend fun addFuelPrice(fuelPrice: FuelPrice) {
        // Deactivate old prices for this fuel type
        fuelPriceDao.deactivateFuelPrices(fuelPrice.fuelType)
        // Add new price
        fuelPriceDao.insertFuelPrice(FuelPriceEntity.fromFuelPrice(fuelPrice))
    }

    suspend fun updateFuelPrice(fuelPrice: FuelPrice) {
        fuelPriceDao.updateFuelPrice(FuelPriceEntity.fromFuelPrice(fuelPrice))
    }

    suspend fun deleteFuelPrice(fuelPrice: FuelPrice) {
        fuelPriceDao.deleteFuelPrice(FuelPriceEntity.fromFuelPrice(fuelPrice))
    }

    // --- Optional: Google Drive Backup (Only for Google Users) ---
    suspend fun backupTripsToDrive(userId: String, accountEmail: String): Boolean {
        return try {
            val allTrips = getAllTrips(userId).first()
            driveService.saveTripsToDrive(accountEmail, allTrips)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun restoreFromDrive(userId: String, accountEmail: String): Boolean {
        return try {
            val backupData = driveService.retrieveAllDataFromDrive(accountEmail)

            // Restore vehicles
            backupData.vehicles?.forEach { vehicle ->
                addVehicle(vehicle, userId)
            }

            // Restore trips
            backupData.trips?.forEach { trip ->
                addTrip(trip, userId)
            }

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
