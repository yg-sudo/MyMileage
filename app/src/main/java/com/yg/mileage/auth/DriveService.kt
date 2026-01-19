/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2025 Yojit Ghadi
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

package com.yg.mileage.auth

import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yg.mileage.Trip
import com.yg.mileage.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

data class DriveBackupData(
    val vehicles: List<Vehicle>?,
    val trips: List<Trip>?,
    val legacyCars: List<String>?
)

class DriveService(private val context: Context) {

    private fun getDriveService(accountEmail: String): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccountName = accountEmail
        return Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("MyMileage") // Replace with your app name
            .build()
    }

    private val carDataFilename = "mymileage_app_cars.json"
    private val vehicleDataFilename = "mymileage_app_vehicles.json"
    private val tripDataFilename = "mymileage_app_trips.json"

    // New methods for Vehicle objects
    suspend fun saveVehiclesToDrive(accountEmail: String, vehicles: List<Vehicle>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val drive = getDriveService(accountEmail)
                val gson = Gson()
                val jsonVehicles = gson.toJson(vehicles)

                // Check if file exists in appDataFolder
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$vehicleDataFilename'")
                    .execute()

                val existingFile = fileList.files.firstOrNull()
                val fileMetadata = File().apply {
                    name = vehicleDataFilename
                    if (existingFile == null) { // Only set parents if creating a new file
                        parents = listOf("appDataFolder")
                    }
                }
                val mediaContent = ByteArrayContent(
                    "application/json",
                    jsonVehicles.toByteArray(StandardCharsets.UTF_8)
                )

                if (existingFile != null) {
                    drive.files().update(existingFile.id, fileMetadata, mediaContent).execute()
                    Log.d("DriveService", "Vehicles updated in Drive: ${existingFile.id}")
                } else {
                    val file: File = drive.files().create(fileMetadata, mediaContent)
                        .setFields("id, parents")
                        .execute()
                    Log.d("DriveService", "Vehicles saved to Drive: ${file.id}")
                }
                true
            } catch (e: Exception) {
                Log.e("DriveService", "Error saving vehicles to Drive", e)
                false
            }
        }

    suspend fun loadVehiclesFromDrive(accountEmail: String): List<Vehicle>? =
        withContext(Dispatchers.IO) {
            if (accountEmail.isBlank()) {
                Log.w("DriveService", "Cannot load vehicles from Drive, account email is blank.")
                return@withContext null
            }
            try {
                val drive = getDriveService(accountEmail)
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$vehicleDataFilename'")
                    .execute()

                val file = fileList.files.firstOrNull()
                    ?: return@withContext emptyList() // No file, return empty or null

                Log.d("DriveService", "Found vehicle data file: ${file.id}")

                val inputStream = drive.files().get(file.id).executeMediaAsInputStream()
                val jsonVehicles = BufferedReader(
                    InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                    )
                ).use { it.readText() }
                val gson = Gson()
                val type = object : TypeToken<List<Vehicle>>() {}.type
                gson.fromJson(jsonVehicles, type)
            } catch (e: Exception) {
                Log.e("DriveService", "Error loading vehicles from Drive", e)
                null // Indicate error
            }
        }

    // Trip management methods
    suspend fun saveTripsToDrive(accountEmail: String, trips: List<Trip>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val drive = getDriveService(accountEmail)
                val gson = Gson()
                val jsonTrips = gson.toJson(trips)

                // Check if file exists in appDataFolder
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$tripDataFilename'")
                    .execute()

                val existingFile = fileList.files.firstOrNull()
                val fileMetadata = File().apply {
                    name = tripDataFilename
                    if (existingFile == null) { // Only set parents if creating a new file
                        parents = listOf("appDataFolder")
                    }
                }
                val mediaContent = ByteArrayContent(
                    "application/json",
                    jsonTrips.toByteArray(StandardCharsets.UTF_8)
                )

                if (existingFile != null) {
                    drive.files().update(existingFile.id, fileMetadata, mediaContent).execute()
                    Log.d("DriveService", "Trips updated in Drive: ${existingFile.id}")
                } else {
                    val file: File = drive.files().create(fileMetadata, mediaContent)
                        .setFields("id, parents")
                        .execute()
                    Log.d("DriveService", "Trips saved to Drive: ${file.id}")
                }
                true
            } catch (e: Exception) {
                Log.e("DriveService", "Error saving trips to Drive", e)
                false
            }
        }

    suspend fun loadTripsFromDrive(accountEmail: String): List<Trip>? =
        withContext(Dispatchers.IO) {
            if (accountEmail.isBlank()) {
                Log.w("DriveService", "Cannot load trips from Drive, account email is blank.")
                return@withContext null
            }
            try {
                val drive = getDriveService(accountEmail)
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$tripDataFilename'")
                    .execute()

                val file = fileList.files.firstOrNull()
                    ?: return@withContext emptyList() // No file, return empty or null

                Log.d("DriveService", "Found trip data file: ${file.id}")

                val inputStream = drive.files().get(file.id).executeMediaAsInputStream()
                val jsonTrips = BufferedReader(
                    InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                    )
                ).use { it.readText() }
                val gson = Gson()
                val type = object : TypeToken<List<Trip>>() {}.type
                gson.fromJson(jsonTrips, type)
            } catch (e: Exception) {
                Log.e("DriveService", "Error loading trips from Drive", e)
                null // Indicate error
            }
        }

    suspend fun retrieveAllDataFromDrive(accountEmail: String): DriveBackupData =
        withContext(Dispatchers.IO) {
            if (accountEmail.isBlank()) {
                Log.w("DriveService", "Cannot retrieve data from Drive, account email is blank.")
                return@withContext DriveBackupData(null, null, null)
            }
            val vehiclesDeferred = async { loadVehiclesFromDrive(accountEmail) }
            val tripsDeferred = async { loadTripsFromDrive(accountEmail) }
            val legacyCarsDeferred = async { loadCarsFromDrive(accountEmail) }

            DriveBackupData(
                vehicles = vehiclesDeferred.await(),
                trips = tripsDeferred.await(),
                legacyCars = legacyCarsDeferred.await()
            )
        }

    // Legacy methods for backward compatibility
    suspend fun saveCarsToDrive(accountEmail: String, cars: List<String>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val drive = getDriveService(accountEmail)
                val gson = Gson()
                val jsonCars = gson.toJson(cars)

                // Check if file exists in appDataFolder
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$carDataFilename'")
                    .execute()

                val existingFile = fileList.files.firstOrNull()
                val fileMetadata = File().apply {
                    name = carDataFilename
                    if (existingFile == null) { // Only set parents if creating a new file
                        parents = listOf("appDataFolder")
                    }
                }
                val mediaContent = ByteArrayContent(
                    "application/json",
                    jsonCars.toByteArray(StandardCharsets.UTF_8)
                )

                if (existingFile != null) {
                    drive.files().update(existingFile.id, fileMetadata, mediaContent).execute()
                    Log.d("DriveService", "Cars updated in Drive: ${existingFile.id}")
                } else {
                    val file: File = drive.files().create(fileMetadata, mediaContent)
                        .setFields("id, parents")
                        .execute()
                    Log.d("DriveService", "Cars saved to Drive: ${file.id}")
                }
                true
            } catch (e: Exception) {
                Log.e("DriveService", "Error saving cars to Drive", e)
                false
            }
        }

    suspend fun loadCarsFromDrive(accountEmail: String): List<String>? =
        withContext(Dispatchers.IO) {
            if (accountEmail.isBlank()) {
                Log.w("DriveService", "Cannot load cars from Drive, account email is blank.")
                return@withContext null
            }
            try {
                val drive = getDriveService(accountEmail)
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$carDataFilename'")
                    .execute()

                val file = fileList.files.firstOrNull()
                    ?: return@withContext emptyList() // No file, return empty or null

                Log.d("DriveService", "Found car data file: ${file.id}")

                val inputStream = drive.files().get(file.id).executeMediaAsInputStream()
                val jsonCars = BufferedReader(
                    InputStreamReader(
                        inputStream,
                        StandardCharsets.UTF_8
                    )
                ).use { it.readText() }
                val gson = Gson()
                val type = object : TypeToken<List<String>>() {}.type
                gson.fromJson(jsonCars, type)
            } catch (e: Exception) {
                Log.e("DriveService", "Error loading cars from Drive", e)
                null // Indicate error
            }
        }
}
