package com.yg.mileage.auth

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
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
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class DriveService(private val context: Context) {

    private fun getDriveService(account: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccount = account.account
        return Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("Mileage Calculator") // Replace with your app name
            .build()
    }

    private val CAR_DATA_FILENAME = "mileage_app_cars.json"
    private val VEHICLE_DATA_FILENAME = "mileage_app_vehicles.json"
    private val TRIP_DATA_FILENAME = "mileage_app_trips.json"


    // New methods for Vehicle objects
    suspend fun saveVehiclesToDrive(account: GoogleSignInAccount, vehicles: List<Vehicle>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val drive = getDriveService(account)
                val gson = Gson()
                val jsonVehicles = gson.toJson(vehicles)

                // Check if file exists in appDataFolder
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$VEHICLE_DATA_FILENAME'")
                    .execute()

                val existingFile = fileList.files.firstOrNull()
                val fileMetadata = File().apply {
                    name = VEHICLE_DATA_FILENAME
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

    suspend fun loadVehiclesFromDrive(account: GoogleSignInAccount): List<Vehicle>? =
        withContext(Dispatchers.IO) {
            try {
                val drive = getDriveService(account)
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$VEHICLE_DATA_FILENAME'")
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
                gson.fromJson<List<Vehicle>>(jsonVehicles, type)
            } catch (e: Exception) {
                Log.e("DriveService", "Error loading vehicles from Drive", e)
                null // Indicate error
            }
        }

    // Trip management methods
    suspend fun saveTripsToDrive(account: GoogleSignInAccount, trips: List<Trip>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val drive = getDriveService(account)
                val gson = Gson()
                val jsonTrips = gson.toJson(trips)

                // Check if file exists in appDataFolder
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$TRIP_DATA_FILENAME'")
                    .execute()

                val existingFile = fileList.files.firstOrNull()
                val fileMetadata = File().apply {
                    name = TRIP_DATA_FILENAME
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

    suspend fun loadTripsFromDrive(account: GoogleSignInAccount): List<Trip>? =
        withContext(Dispatchers.IO) {
            try {
                val drive = getDriveService(account)
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$TRIP_DATA_FILENAME'")
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
                gson.fromJson<List<Trip>>(jsonTrips, type)
            } catch (e: Exception) {
                Log.e("DriveService", "Error loading trips from Drive", e)
                null // Indicate error
            }
        }

    // Legacy methods for backward compatibility
    suspend fun saveCarsToDrive(account: GoogleSignInAccount, cars: List<String>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val drive = getDriveService(account)
                val gson = Gson()
                val jsonCars = gson.toJson(cars)

                // Check if file exists in appDataFolder
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$CAR_DATA_FILENAME'")
                    .execute()

                val existingFile = fileList.files.firstOrNull()
                val fileMetadata = File().apply {
                    name = CAR_DATA_FILENAME
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

    suspend fun loadCarsFromDrive(account: GoogleSignInAccount): List<String>? =
        withContext(Dispatchers.IO) {
            try {
                val drive = getDriveService(account)
                val fileList: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("nextPageToken, files(id, name)")
                    .setQ("name='$CAR_DATA_FILENAME'")
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
                gson.fromJson<List<String>>(jsonCars, type)
            } catch (e: Exception) {
                Log.e("DriveService", "Error loading cars from Drive", e)
                null // Indicate error
            }
        }
}