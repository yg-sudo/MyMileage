package com.yg.mileage.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security // Added for SecuritySettings
import androidx.compose.material.icons.filled.AttachMoney // Added for CurrencySettings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object TripDetails : Screen("trip_details", "Trip Details", Icons.Filled.Calculate)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object TripLog : Screen("trip_log", "Trip Log", Icons.Filled.History)
    object AddVehicle : Screen("add_vehicle", "Add Vehicle", Icons.Filled.Person)
    object Account : Screen("account", "Account", Icons.Filled.AccountCircle)
    object PersonalInfo : Screen("personal_info", "Personal Info", Icons.Filled.AccountCircle) // Placeholder icon
    object SecuritySettings : Screen("security_settings", "Security Settings", Icons.Filled.Security)
    object CurrencySettings : Screen("currency_settings", "Currency & Fuel Prices", Icons.Filled.AttachMoney)
}

val bottomNavItems = listOf(
    Screen.Profile,
    Screen.TripLog
)
