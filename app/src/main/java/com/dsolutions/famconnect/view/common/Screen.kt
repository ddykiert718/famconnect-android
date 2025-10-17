package com.dsolutions.famconnect.view.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Calendar : Screen("calendar", "Kalender", Icons.Default.Event)
    object Family : Screen("family", "Familie", Icons.Default.Group)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Settings : Screen("settings", "Einstellungen", Icons.Default.Settings)
    object Tasks : Screen("tasks", "Aufgaben", Icons.Default.List)
    object Meals : Screen("meals", "Essensplan", Icons.Default.Restaurant)
    object Shopping : Screen("shopping", "Einkauf", Icons.Default.ShoppingCart)
    object TimePlan : Screen("timeplan", "Zeitplan", Icons.Default.Schedule)
    object Vacation : Screen("vacation", "Urlaub", Icons.Default.BeachAccess)
    object Login : Screen("login", "Login", Icons.Default.Home)
    object Register : Screen("register", "Register", Icons.Default.Home)
}
