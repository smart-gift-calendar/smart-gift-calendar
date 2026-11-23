package com.smartgiftcalendar.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartgiftcalendar.app.ui.screens.AdminScreen
import com.smartgiftcalendar.app.ui.screens.CalendarScreen
import com.smartgiftcalendar.app.ui.screens.CompanyScreen
import com.smartgiftcalendar.app.ui.screens.InviteScreen
import com.smartgiftcalendar.app.ui.screens.ShopScreen

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun SmartGiftApp() {
    val navController = rememberNavController()
    val items = listOf(
        NavItem("calendar", "Calendar", Icons.Filled.CalendarMonth),
        NavItem("shop", "Shop", Icons.Filled.CardGiftcard),
        NavItem("invite", "Invite", Icons.Filled.PersonAdd),
        NavItem("admin", "Admin", Icons.Filled.Store),
        NavItem("company", "Company", Icons.Filled.Business)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "calendar",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("calendar") { CalendarScreen() }
            composable("shop") { ShopScreen() }
            composable("invite") { InviteScreen() }
            composable("admin") { AdminScreen() }
            composable("company") { CompanyScreen() }
        }
    }
}
