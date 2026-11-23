package com.smartgiftcalendar.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartgiftcalendar.app.data.FakeRepository
import java.time.LocalDate

@Composable
fun InviteScreen() {
    val user = FakeRepository.currentUser.value
    val inviteLink = "https://smartgiftcalendar.app/invite/${user.inviteCode}"
    var friendName by remember { mutableStateOf("") }
    var friendBirthday by remember { mutableStateOf("") }
    var inviteCodeInput by remember { mutableStateOf(user.inviteCode) }
    var message by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Invite Friends", style = MaterialTheme.typography.headlineMedium)
            Text("Share your personal invite link. Friends who join get a 10% discount code.")
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your invite link", style = MaterialTheme.typography.titleMedium)
                    Text(inviteLink)
                    Text("Invite code: ${user.inviteCode}")
                    if (user.hasInviteDiscount) {
                        Text("You received a FRIEND10 discount for an invite registration.")
                    }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Friend registers through link", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = inviteCodeInput, onValueChange = { inviteCodeInput = it }, label = { Text("Invite code") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = friendName, onValueChange = { friendName = it }, label = { Text("Friend name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = friendBirthday, onValueChange = { friendBirthday = it }, label = { Text("Friend birthday (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                    Button(onClick = {
                        val date = runCatching { LocalDate.parse(friendBirthday.trim()) }.getOrNull()
                        when {
                            friendName.isBlank() -> message = "Friend name is required."
                            date == null -> message = "Use date format YYYY-MM-DD."
                            else -> {
                                val ok = FakeRepository.registerThroughInvite(friendName.trim(), inviteCodeInput, date)
                                message = if (ok) {
                                    "Registered! ${friendName.trim()} was added and FRIEND10 discount is available."
                                } else {
                                    "Invalid invite code."
                                }
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("Register friend") }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recent invite registrations", style = MaterialTheme.typography.titleMedium)
                    if (FakeRepository.inviteRegistrations.isEmpty()) {
                        Text("No registrations yet.")
                    } else {
                        FakeRepository.inviteRegistrations.forEach { Text("• $it") }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
