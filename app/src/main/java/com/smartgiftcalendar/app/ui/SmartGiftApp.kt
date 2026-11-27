package com.smartgiftcalendar.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartgiftcalendar.app.R
import com.smartgiftcalendar.app.data.AuthRepository
import com.smartgiftcalendar.app.data.UserRole
import com.smartgiftcalendar.app.ui.screens.AdminScreen
import com.smartgiftcalendar.app.ui.screens.CalendarScreen
import com.smartgiftcalendar.app.ui.screens.CompanyScreen
import com.smartgiftcalendar.app.ui.screens.DashboardScreen
import com.smartgiftcalendar.app.ui.screens.InviteScreen
import com.smartgiftcalendar.app.ui.screens.LoginScreen
import com.smartgiftcalendar.app.ui.screens.ShopScreen

@Composable
fun SmartGiftApp() {
    val account = AuthRepository.currentAccount.value
    // Show login first when no user is authenticated.
    if (account == null) {
        LoginScreen()
    } else {
        AuthenticatedApp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route
    val account = AuthRepository.currentAccount.value

    Scaffold(
        topBar = {
            if (route != "dashboard") {
                TopAppBar(
                    title = { Text(routeTitle(route)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                DashboardScreen(
                    onOpenCalendar = { navController.navigate("calendar") },
                    onOpenInvite = { navController.navigate("invite") },
                    onOpenShop = { navController.navigate("shop") },
                    onOpenAdmin = { navController.navigate("admin") },
                    onOpenCompany = { navController.navigate("company") }
                )
            }
            composable("calendar") { CalendarScreen() }
            composable("invite") { InviteScreen() }
            composable("shop") { ShopScreen() }

            // Role-protected screens: normal users cannot open these panels.
            composable("admin") {
                if (account?.role == UserRole.ADMIN) AdminScreen() else AccessDenied()
            }
            composable("company") {
                if (account?.role == UserRole.COMPANY) CompanyScreen() else AccessDenied()
            }
        }
    }
}

@Composable
private fun AccessDenied() {
    Text(stringResource(R.string.access_denied), modifier = Modifier.padding(16.dp))
}

@Composable
private fun routeTitle(route: String?): String = when (route) {
    "calendar" -> stringResource(R.string.calendar)
    "invite" -> stringResource(R.string.invite_link)
    "shop" -> stringResource(R.string.shop)
    "admin" -> stringResource(R.string.admin_panel)
    "company" -> stringResource(R.string.company_panel)
    else -> stringResource(R.string.app_name)
}
