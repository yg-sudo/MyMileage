package com.yg.mileage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.yg.mileage.auth.SignInResult
import com.yg.mileage.auth.UserData
import com.yg.mileage.data.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarViewModel(
    private val repository: Repository
) : ViewModel() {
    private val _savedVehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val savedVehicles: StateFlow<List<Vehicle>> = _savedVehicles.asStateFlow()

    private val _savedTrips = MutableStateFlow<List<Trip>>(emptyList())
    val savedTrips: StateFlow<List<Trip>> = _savedTrips.asStateFlow()

    private val _editingTrip = MutableStateFlow<Trip?>(null)
    val editingTrip = _editingTrip.asStateFlow()

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
    private var vehicleJob: Job? = null
    private var currencyJob: Job? = null
    private var fuelPriceJob: Job? = null
    private var currentUserId: String? = null

    fun observeUserData(userId: String) {
        currentUserId = userId
        tripJob?.cancel()
        vehicleJob?.cancel()
        currencyJob?.cancel()
        fuelPriceJob?.cancel()
        
        tripJob = viewModelScope.launch {
            repository.getAllTrips(userId).collect { trips -> _savedTrips.value = trips }
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
        vehicleJob?.cancel()
        currencyJob?.cancel()
        fuelPriceJob?.cancel()
        currentUserId = null
        _savedTrips.value = emptyList()
        _savedVehicles.value = emptyList()
        _currencies.value = emptyList()
        _fuelPrices.value = emptyList()
        _defaultCurrency.value = null
        _editingTrip.value = null
    }

    fun setEditingTrip(trip: Trip?) {
        _editingTrip.value = trip
    }

    suspend fun addTrip(trip: Trip, googleAccount: GoogleSignInAccount?) {
        val googleId = googleAccount?.id
        val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid
        val userIdToUse = googleId ?: firebaseUid

        Log.d("CarViewModel", "addTrip called. GoogleSignInAccount: ${googleAccount != null}, Google ID: $googleId, Firebase UID: $firebaseUid, Using userId: $userIdToUse")

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
            backupIfGoogleUser(userIdToUse, googleAccount, trip)
        } catch (e: Exception) {
            Log.e("CarViewModel", "Error adding trip via repository for userId '$userIdToUse'", e)
        }
    }

    suspend fun updateTrip(trip: Trip, googleAccount: GoogleSignInAccount?) {
        val googleId = googleAccount?.id
        val firebaseUid = FirebaseAuth.getInstance().currentUser?.uid
        val userIdToUse = googleId ?: firebaseUid

        Log.d("CarViewModel", "updateTrip called. GoogleSignInAccount: ${googleAccount != null}, Google ID: $googleId, Firebase UID: $firebaseUid, Using userId: $userIdToUse")

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
            backupIfGoogleUser(userIdToUse, googleAccount, trip)
        } catch (e: Exception) {
            Log.e("CarViewModel", "Error updating trip via repository for userId '$userIdToUse'", e)
        }
    }

    private suspend fun backupIfGoogleUser(userId: String, googleAccount: GoogleSignInAccount?, trip: Trip) {
        val user = FirebaseAuth.getInstance().currentUser
        val isGoogleUser = user?.providerData?.any { it.providerId == "google.com" } == true
        // Only back up completed trips to avoid uploading drafts
        if (isGoogleUser && googleAccount != null && trip.status == TripStatus.COMPLETED) {
            repository.backupTripsToDrive(userId, googleAccount)
        }
    }

    fun clearEditingTrip() {
        _editingTrip.value = null
    }

    suspend fun deleteTrip(tripId: String) {
        currentUserId?.let { repository.deleteTrip(tripId, it) }
    }

    suspend fun addVehicle(vehicle: Vehicle) {
        currentUserId?.let { repository.addVehicle(vehicle, it) }
    }
    suspend fun updateVehicle(vehicle: Vehicle) {
        currentUserId?.let { repository.updateVehicle(vehicle, it) }
    }
    suspend fun deleteVehicle(vehicleName: String) {
        currentUserId?.let { repository.deleteVehicle(vehicleName, it) }
    }
    suspend fun canDeleteVehicle(vehicleName: String): Boolean {
        return currentUserId?.let { repository.canDeleteVehicle(vehicleName, it) } ?: false
    }

    fun onSignInResult(result: SignInResult) {
        _currentUser.value = result.data
        val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            observeUserData(userId)
            viewModelScope.launch { _signInCompleted.emit(Unit) }
        } else clearUserData()
    }
    fun updateSignInState(user: UserData?) {
        _currentUser.value = user
        val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) observeUserData(userId) else clearUserData()
    }

    fun isGoogleUser(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.providerData?.any { it.providerId == "google.com" } == true
    }

    // Manual backup if you want it via button (optional):
    fun backupTripsToDrive(googleAccount: GoogleSignInAccount?, onResult: (Boolean, String) -> Unit) {
        if (googleAccount == null) {
            onResult(false, "Google Account required for Drive backup.")
            return
        }
        val userId = currentUserId
        if (userId == null) {
            onResult(false, "No authenticated user found.")
            return
        }
        viewModelScope.launch {
            val success = repository.backupTripsToDrive(userId, googleAccount)
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
}
