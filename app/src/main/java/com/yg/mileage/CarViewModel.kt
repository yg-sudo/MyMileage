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
package com.yg.mileage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.yg.mileage.auth.SignInResult
import com.yg.mileage.auth.UserData
import com.yg.mileage.data.Repository
import com.yg.mileage.data.TripGroupEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class CarViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _savedVehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val savedVehicles: StateFlow<List<Vehicle>> = _savedVehicles.asStateFlow()

    private val _savedTrips = MutableStateFlow<List<Trip>>(emptyList())
    val savedTrips: StateFlow<List<Trip>> = _savedTrips.asStateFlow()

    private val _tripGroups = MutableStateFlow<List<TripGroupEntity>>(emptyList())
    val tripGroups: StateFlow<List<TripGroupEntity>> = _tripGroups.asStateFlow()

    private val _editingTrip = MutableStateFlow<Trip?>(null)
    val editingTrip = _editingTrip.asStateFlow()

    private val _editingTripGroupId = MutableStateFlow<String?>(null)
    val editingTripGroupId = _editingTripGroupId.asStateFlow()

    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser.asStateFlow()

    private val _signInCompleted = MutableSharedFlow<Unit>()
    val signInCompleted = _signInCompleted.asSharedFlow()

    private val _currencies = MutableStateFlow<List<Currency>>(emptyList())
    val currencies: StateFlow<List<Currency>> = _currencies.asStateFlow()

    private val _fuelPrices = MutableStateFlow<List<FuelPrice>>(emptyList())
    val fuelPrices: StateFlow<List<FuelPrice>> = _fuelPrices.asStateFlow()

    private val _defaultCurrency = MutableStateFlow<Currency?>(null)
    val defaultCurrency: StateFlow<Currency?> = _defaultCurrency.asStateFlow()

    private var tripJob: Job? = null
    private var tripGroupJob: Job? = null
    private var vehicleJob: Job? = null
    private var currencyJob: Job? = null
    private var fuelPriceJob: Job? = null
    private var currentUserId: String? = null

    fun observeUserData(userId: String) {
        currentUserId = userId
        tripJob?.cancel()
        tripGroupJob?.cancel()
        vehicleJob?.cancel()
        currencyJob?.cancel()
        fuelPriceJob?.cancel()
        
        tripJob = viewModelScope.launch {
            repository.getAllTrips(userId).collect { trips -> _savedTrips.value = trips }
        }
        tripGroupJob = viewModelScope.launch {
            repository.getAllTripGroups(userId).collect { groups -> _tripGroups.value = groups }
        }
        vehicleJob = viewModelScope.launch {
            repository.getAllVehicles(userId).collect { vehicles -> _savedVehicles.value = vehicles }
        }
        currencyJob = viewModelScope.launch {
            repository.getAllCurrencies().collect { currencies -> _currencies.value = currencies }
        }
        fuelPriceJob = viewModelScope.launch {
            repository.getAllActiveFuelPrices().collect { fuelPrices -> _fuelPrices.value = fuelPrices }
        }
        
        // Load default currency
        viewModelScope.launch {
            _defaultCurrency.value = repository.getDefaultCurrency()
        }
    }

    fun clearUserData() {
        tripJob?.cancel()
        tripGroupJob?.cancel()
        vehicleJob?.cancel()
        currencyJob?.cancel()
        fuelPriceJob?.cancel()
        currentUserId = null
        _savedTrips.value = emptyList()
        _tripGroups.value = emptyList()
        _savedVehicles.value = emptyList()
        _currencies.value = emptyList()
        _fuelPrices.value = emptyList()
        _defaultCurrency.value = null
        _editingTrip.value = null
    }

    suspend fun addTripGroup(groupName: String) {
        val userIdToUse = currentUserId ?: return
        val newGroup = TripGroupEntity(
            id = UUID.randomUUID().toString(),
            userId = userIdToUse,
            groupName = groupName,
            createdAt = Date(),
            updatedAt = Date()
        )
        repository.addTripGroup(newGroup)
    }

    fun setEditingTrip(trip: Trip?) {
        _editingTrip.value = trip
    }

    fun setEditingTripGroupId(groupId: String?) {
        _editingTripGroupId.value = groupId
    }

    suspend fun addTrip(trip: Trip) {
        val userIdToUse = currentUserId

        Log.d("CarViewModel", "addTrip called. Using userId: $userIdToUse")

        if (userIdToUse == null) {
            Log.e("CarViewModel", "addTrip: userIdToUse is NULL. Cannot save trip. Trip data: $trip")
            return
        }
        if (userIdToUse.isBlank()) {
            Log.w("CarViewModel", "addTrip: userIdToUse is BLANK. This might cause issues. Trip data: $trip")
        }

        Log.d("CarViewModel", "Attempting to add trip for actual userId: '$userIdToUse', Trip data: $trip")
        try {
            repository.addTrip(trip, userIdToUse)
            backupIfGoogleUser(userIdToUse, trip)
        } catch (e: Exception) {
            Log.e("CarViewModel", "Error adding trip via repository for userId '$userIdToUse'", e)
        }
    }

    suspend fun updateTrip(trip: Trip) {
        val userIdToUse = currentUserId

        Log.d("CarViewModel", "updateTrip called. Using userId: $userIdToUse")

        if (userIdToUse == null) {
            Log.e("CarViewModel", "updateTrip: userIdToUse is NULL. Cannot update trip. Trip data: $trip")
            return
        }
        if (userIdToUse.isBlank()) {
            Log.w("CarViewModel", "updateTrip: userIdToUse is BLANK. This might cause issues. Trip data: $trip")
        }
        Log.d("CarViewModel", "Attempting to update trip for actual userId: '$userIdToUse', Trip data: $trip")
        try {
            repository.updateTrip(trip, userIdToUse)
            backupIfGoogleUser(userIdToUse, trip)
        } catch (e: Exception) {
            Log.e("CarViewModel", "Error updating trip via repository for userId '$userIdToUse'", e)
        }
    }

    private suspend fun backupIfGoogleUser(userId: String, trip: Trip) {
        val user = FirebaseAuth.getInstance().currentUser
        val isGoogleUser = user?.providerData?.any { it.providerId == "google.com" } == true
        // Only back up completed trips to avoid uploading drafts
        if (isGoogleUser && trip.status == TripStatus.COMPLETED && user.email != null) {
            repository.backupTripsToDrive(userId, user.email!!)
        }
    }

    fun clearEditingTrip() {
        _editingTrip.value = null
        _editingTripGroupId.value = null
    }

    suspend fun deleteTrip(tripId: String) {
        currentUserId?.let { repository.deleteTrip(tripId, it) }
    }

    suspend fun saveTripGroup(groupName: String) {
        val userId = currentUserId
        if (userId != null) {
            val now = Date()
            val newGroup = TripGroupEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                groupName = groupName,
                createdAt = now,
                updatedAt = now
            )
            repository.saveTripGroup(newGroup)
        }
    }

    suspend fun updateTripGroup(tripGroup: TripGroupEntity) {
        repository.updateTripGroup(tripGroup)
    }

    suspend fun deleteTripGroup(tripGroup: TripGroupEntity) {
        repository.deleteTripGroup(tripGroup)
    }

    suspend fun addVehicle(vehicle: Vehicle) {
        currentUserId?.let { repository.addVehicle(vehicle, it) }
    }
    suspend fun updateVehicle(vehicle: Vehicle) {
        currentUserId?.let { repository.updateVehicle(vehicle, it) }
    }
    suspend fun deleteVehicle(vehicleId: String): Boolean {
        val canDelete = canDeleteVehicle(vehicleId)
        if (canDelete) {
            currentUserId?.let { repository.deleteVehicle(vehicleId, it) }
        } 
        return canDelete
    }
    suspend fun canDeleteVehicle(vehicleId: String): Boolean {
        return currentUserId?.let { repository.canDeleteVehicle(vehicleId, it) } ?: false
    }

    fun onSignInResult(result: SignInResult) {
        _currentUser.value = result.data
        val userId: String? = result.data?.userId
        if (userId != null) {
            observeUserData(userId)
            viewModelScope.launch { _signInCompleted.emit(Unit) }
        } else clearUserData()
    }
    fun updateSignInState(user: UserData?) {
        _currentUser.value = user
        val userId: String? = user?.userId
        if (userId != null) observeUserData(userId) else clearUserData()
    }

    fun isGoogleUser(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.providerData?.any { it.providerId == "google.com" } == true
    }

    // Manual backup if you want it via button (optional):
    fun backupTripsToDrive(onResult: (Boolean, String) -> Unit) {
        val userId = currentUserId
        if (userId == null) {
            onResult(false, "No authenticated user found.")
            return
        }
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        if (email == null) {
            onResult(false, "Google Account email not found.")
            return
        }
        viewModelScope.launch {
            val success = repository.backupTripsToDrive(userId, email)
            onResult(success, if (success) "Backup successful." else "Backup failed.")
        }
    }

    // Currency functions
    suspend fun addCurrency(currency: Currency) {
        repository.addCurrency(currency)
    }

    suspend fun updateCurrency(currency: Currency) {
        repository.updateCurrency(currency)
    }

    suspend fun deleteCurrency(currency: Currency) {
        repository.deleteCurrency(currency)
    }

    suspend fun setDefaultCurrency(currencyId: String) {
        repository.setDefaultCurrency(currencyId)
        _defaultCurrency.value = repository.getDefaultCurrency()
    }

    // Fuel price functions
    suspend fun addFuelPrice(fuelPrice: FuelPrice) {
        repository.addFuelPrice(fuelPrice)
    }

    suspend fun updateFuelPrice(fuelPrice: FuelPrice) {
        repository.updateFuelPrice(fuelPrice)
    }

    suspend fun deleteFuelPrice(fuelPrice: FuelPrice) {
        repository.deleteFuelPrice(fuelPrice)
    }

    suspend fun getLatestFuelPrice(fuelType: FuelType): FuelPrice? {
        return repository.getLatestFuelPrice(fuelType)
    }

    fun changePassword(password: String, onResult: (Boolean, String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.updatePassword(password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    val errorMsg = if (task.exception is com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
                        "For security, please sign out and sign in again to change your password."
                    } else {
                        task.exception?.message ?: "Unknown error"
                    }
                    onResult(false, errorMsg)
                }
            }
        } else {
            onResult(false, "No user signed in.")
        }
    }
}
