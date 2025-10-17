package com.dsolutions.famconnect.view

import com.dsolutions.famconnect.view.common.PlaceholderScreen
import com.dsolutions.famconnect.view.calendar.CalendarScreen
import com.dsolutions.famconnect.view.user.RegisterScreen
import com.dsolutions.famconnect.viewmodel.EventViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dsolutions.famconnect.view.common.Screen
import com.dsolutions.famconnect.view.user.FamilyScreen
import com.dsolutions.famconnect.view.user.LoginScreen
import com.dsolutions.famconnect.viewmodel.UserViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlin.text.isNotBlank

@Composable
fun MainScreen(
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel,
) {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Family, Screen.Calendar, Screen.Settings)

    // Sammeln Sie den Benutzerstatus aus dem UserViewModel
    val userState by userViewModel.userState.collectAsState() // Annahme: Sie haben so etwas in UserViewModel

    val familyId = userState?.familyId
    val userId = userState?.id

    // `LaunchedEffect` wird ausgeführt, wenn sich userState.familyId ändert.
    LaunchedEffect(familyId) {
        // Nur laden, wenn eine gültige familyId vorhanden ist
        familyId?.let { famId ->
            if (famId.isNotBlank()) {
                eventViewModel.startLoadingEvents(famId)
            }
        } ?: run {
            // Wenn der Benutzer sich abmeldet (familyId ist null), stoppen Sie das Laden.
            eventViewModel.stopLoadingEvents()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route


                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }

    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateTo = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            // Verhindert doppeltes Navigieren zum gleichen Ziel
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                        }
                    }
                )
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(eventViewModel = eventViewModel, userId = userId)
            }
            composable(Screen.Family.route) {
                FamilyScreen(
                    userViewModel = userViewModel,
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) {
                        // Wichtig: Verhindere, dass der Back-Stack wächst,
                        // wenn der User nicht eingeloggt ist.
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    } }
                ) // ToDo: implementieren
            }
            composable(Screen.Settings.route) {
                //SettingsScreen() // ToDo: implementieren
            }
            composable(Screen.Tasks.route) {
                PlaceholderScreen("Tasks")
            }
            composable(Screen.Meals.route) {
                PlaceholderScreen("Meals")
            }
            composable(Screen.Shopping.route) {
                PlaceholderScreen("Shopping List")
            }
            composable(Screen.TimePlan.route) {
                PlaceholderScreen("Time Plan")
            }
            composable(Screen.Vacation.route) {
                PlaceholderScreen("Vacation")
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    userViewModel = userViewModel,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = {
                        // Nach erfolgreichem Login zurück zur Hauptansicht
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    userViewModel = userViewModel,
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    familySelection = null,
                    familyIdOrName = null,
                    familyPin = null,
                    prefilledLastName = null
                )
            }
        }
    }
}

@Composable
fun MainMenuButton(
    text: String,
    drawableRes: Int,
    //icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp,
            bottomStart = 8.dp,
            bottomEnd = 8.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        elevation = ButtonDefaults.buttonElevation(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            //Icon(imageVector = icon, contentDescription = null)
            Image(
                painter = painterResource(drawableRes),
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(60.dp)
                    .padding(2.dp)
            )
            Text(text, style = MaterialTheme.typography.titleMedium)
        }
    }
}
