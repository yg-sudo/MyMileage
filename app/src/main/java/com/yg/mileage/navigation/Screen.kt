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

package com.yg.mileage.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector? = null,
    val description: String
) {
    object TripDetails : Screen("trip_details", "Trip Details", Icons.Filled.Calculate, "Create new trips")
    object Vehicles : Screen("profile", "Vehicles", Icons.Rounded.DirectionsCar, "View and manage vehicles")
    object Trips : Screen("trip_log", "Trips", Icons.Rounded.LocationOn, "View and manage trips")
    object AddVehicle : Screen("add_vehicle", "Add Vehicle", Icons.Filled.Person, "Add a new vehicle")
    object Account : Screen("account", "Account", Icons.Filled.AccountCircle, "Manage your account")
    object PersonalInfo : Screen("personal_info", "Personal Info", Icons.Filled.AccountCircle, "Edit your personal info")
    object SecuritySettings : Screen("security_settings", "Security Settings", Icons.Filled.Security, "Edit your security settings")
    object CurrencySettings : Screen("currency_settings", "Currency & Fuel Prices", Icons.Filled.AttachMoney, "Add and manage currencies")
    object Activities : Screen("activities", "Stats", Icons.Rounded.CalendarMonth, "View stats")
    object SignIn : Screen("sign_in", "Sign In", null, "Sign in to your account")
    object SignUp : Screen("sign_up", "Sign Up", null, "Sign up for a new account")
}

val bottomNavItems = listOf(
    Screen.Vehicles,
    Screen.Trips,
    Screen.Activities,
)
