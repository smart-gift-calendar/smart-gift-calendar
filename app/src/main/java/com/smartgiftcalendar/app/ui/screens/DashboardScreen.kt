package com.smartgiftcalendar.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartgiftcalendar.app.data.AuthRepository
import com.smartgiftcalendar.app.data.UserRole

@Composable
fun DashboardScreen(
    onOpenCalendar: () -> Unit,
    onOpenInvite: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenAdmin: () -> Unit,
    onOpenCompany: () -> Unit
) {
    val account = AuthRepository.currentAccount.value ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Dashboard", style = MaterialTheme.typography.headlineMedium)
            // Welcome message with name and role.
            Text("Welcome, ${account.displayName}!", style = MaterialTheme.typography.titleLarge)
            Text("Role: ${account.role.label}", style = MaterialTheme.typography.bodyMedium)
        }

        item { DashboardCard("Calendar", "View upcoming birthdays and reminders", onOpenCalendar) }
        item { DashboardCard("Invite link", "Share your invite link with friends", onOpenInvite) }
        item { DashboardCard("Shop", "Browse gifts and place orders", onOpenShop) }

        // Show role-specific panels only when allowed.
        if (account.role == UserRole.ADMIN) {
            item { DashboardCard("Admin panel", "Manage orders, gifts, and discount codes", onOpenAdmin) }
        }
        if (account.role == UserRole.COMPANY) {
            item { DashboardCard("Company panel", "Manage employee events and company orders", onOpenCompany) }
        }

        item {
            Button(onClick = { AuthRepository.logout() }, modifier = Modifier.fillMaxWidth()) {
                Text("Logout")
            }
        }
    }
}

@Composable
private fun DashboardCard(title: String, description: String, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
