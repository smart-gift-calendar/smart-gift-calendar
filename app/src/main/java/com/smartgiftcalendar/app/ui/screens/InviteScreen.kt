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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smartgiftcalendar.app.R
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

    val friendNameRequired = stringResource(R.string.friend_name_required)
    val dateFormatError = stringResource(R.string.date_format_error)
    val invalidInvite = stringResource(R.string.invalid_invite_code)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(stringResource(R.string.invite_friends), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.invite_friends_desc))
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.your_invite_link), style = MaterialTheme.typography.titleMedium)
                    Text(inviteLink)
                    Text(stringResource(R.string.invite_code_label, user.inviteCode))
                    if (user.hasInviteDiscount) {
                        Text(stringResource(R.string.friend10_received))
                    }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.friend_registers), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = inviteCodeInput, onValueChange = { inviteCodeInput = it }, label = { Text(stringResource(R.string.invite_code)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = friendName, onValueChange = { friendName = it }, label = { Text(stringResource(R.string.friend_name)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = friendBirthday, onValueChange = { friendBirthday = it }, label = { Text(stringResource(R.string.friend_birthday)) }, modifier = Modifier.fillMaxWidth())
                    message?.let { msg ->
                        val text = if (msg.startsWith("OK:")) stringResource(R.string.friend_registered, msg.removePrefix("OK:")) else msg
                        Text(text, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(onClick = {
                        val date = runCatching { LocalDate.parse(friendBirthday.trim()) }.getOrNull()
                        when {
                            friendName.isBlank() -> message = friendNameRequired
                            date == null -> message = dateFormatError
                            else -> {
                                val ok = FakeRepository.registerThroughInvite(friendName.trim(), inviteCodeInput, date)
                                message = if (ok) "OK:${friendName.trim()}" else invalidInvite
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.register_friend)) }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.recent_invite_registrations), style = MaterialTheme.typography.titleMedium)
                    if (FakeRepository.inviteRegistrations.isEmpty()) {
                        Text(stringResource(R.string.no_registrations))
                    } else {
                        FakeRepository.inviteRegistrations.forEach { (name, code) ->
                            Text("• ${stringResource(R.string.invite_registration_item, name, code)}")
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
