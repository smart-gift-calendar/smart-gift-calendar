package com.smartgiftcalendar.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.smartgiftcalendar.app.data.model.BirthdayRelation
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    var showAddForm by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var relation by remember { mutableStateOf(BirthdayRelation.FRIEND) }
    var relationExpanded by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val upcoming = FakeRepository.upcomingBirthdays()
    val reminders = FakeRepository.remindersForToday()
    val nameRequired = stringResource(R.string.name_required)
    val dateFormatError = stringResource(R.string.date_format_error)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(stringResource(R.string.smart_calendar), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.upcoming_birthdays_reminders), style = MaterialTheme.typography.bodyMedium)
        }

        if (reminders.isNotEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.reminders), style = MaterialTheme.typography.titleMedium)
                        reminders.forEach { (entry, days) ->
                            val msg = if (days == 0L) stringResource(R.string.reminder_today, entry.name)
                            else stringResource(R.string.reminder_three_days, entry.name)
                            Text("• $msg")
                        }
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.upcoming_events), style = MaterialTheme.typography.titleLarge)
                TextButton(onClick = { showAddForm = !showAddForm }) {
                    Text(if (showAddForm) stringResource(R.string.close) else stringResource(R.string.add_birthday))
                }
            }
        }

        if (showAddForm) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.register_birthday), style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.name)) }, modifier = Modifier.fillMaxWidth())
                        ExposedDropdownMenuBox(expanded = relationExpanded, onExpandedChange = { relationExpanded = !relationExpanded }) {
                            OutlinedTextField(
                                value = stringResource(relation.labelRes),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.relation)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relationExpanded) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = relationExpanded, onDismissRequest = { relationExpanded = false }) {
                                BirthdayRelation.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(stringResource(option.labelRes)) },
                                        onClick = { relation = option; relationExpanded = false }
                                    )
                                }
                            }
                        }
                        OutlinedTextField(value = dateText, onValueChange = { dateText = it }, label = { Text(stringResource(R.string.date_yyyy_mm_dd)) }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text(stringResource(R.string.note)) }, modifier = Modifier.fillMaxWidth())
                        formError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        Button(onClick = {
                            val parsedDate = runCatching { LocalDate.parse(dateText.trim()) }.getOrNull()
                            when {
                                name.isBlank() -> formError = nameRequired
                                parsedDate == null -> formError = dateFormatError
                                else -> {
                                    FakeRepository.addBirthday(name.trim(), relation, parsedDate, note.trim())
                                    name = ""; dateText = ""; note = ""; relation = BirthdayRelation.FRIEND
                                    formError = null; showAddForm = false
                                }
                            }
                        }) { Text(stringResource(R.string.save_birthday)) }
                    }
                }
            }
        }

        items(upcoming, key = { it.id }) { entry ->
            val days = FakeRepository.daysUntil(entry.date)
            val next = FakeRepository.nextOccurrence(entry.date)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${entry.name} — ${stringResource(entry.relation.labelRes)}", style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.date_label, next.toString()))
                    Text(
                        when (days) {
                            0L -> stringResource(R.string.today_reminder)
                            3L -> stringResource(R.string.in_three_days_reminder)
                            else -> stringResource(R.string.in_days, days.toInt())
                        }
                    )
                    if (entry.note.isNotBlank()) Text(stringResource(R.string.note_label, entry.note))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
