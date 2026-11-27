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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smartgiftcalendar.app.R
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
            Text(stringResource(R.string.dashboard), style = MaterialTheme.typography.headlineMedium)
            // Welcome message with name and role.
            Text(stringResource(R.string.welcome_user, account.displayName), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.role_label, stringResource(account.role.labelRes)), style = MaterialTheme.typography.bodyMedium)
        }

        item { DashboardCard(stringResource(R.string.calendar), stringResource(R.string.calendar_desc), onOpenCalendar) }
        item { DashboardCard(stringResource(R.string.invite_link), stringResource(R.string.invite_link_desc), onOpenInvite) }
        item { DashboardCard(stringResource(R.string.shop), stringResource(R.string.shop_desc), onOpenShop) }

        // Show role-specific panels only when allowed.
        if (account.role == UserRole.ADMIN) {
            item { DashboardCard(stringResource(R.string.admin_panel), stringResource(R.string.admin_panel_desc), onOpenAdmin) }
        }
        if (account.role == UserRole.COMPANY) {
            item { DashboardCard(stringResource(R.string.company_panel), stringResource(R.string.company_panel_desc), onOpenCompany) }
        }

        item {
            Button(onClick = { AuthRepository.logout() }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.logout))
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
