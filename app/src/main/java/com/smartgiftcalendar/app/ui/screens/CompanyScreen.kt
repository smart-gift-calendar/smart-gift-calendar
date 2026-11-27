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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import com.smartgiftcalendar.app.data.model.CompanyEventType
import com.smartgiftcalendar.app.data.model.Gift
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyScreen() {
    var companyName by remember { mutableStateOf("Acme Inc") }
    var employeeName by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf(CompanyEventType.BIRTHDAY) }
    var typeExpanded by remember { mutableStateOf(false) }
    var selectedGift by remember { mutableStateOf<Gift?>(FakeRepository.gifts.firstOrNull()) }
    var recipientName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    val invalidCompanyEventError = stringResource(R.string.invalid_company_event_error)
    val employeeEventAdded = stringResource(R.string.employee_event_added)
    val companyOrderError = stringResource(R.string.company_order_error)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(stringResource(R.string.company_panel_title), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.company_panel_subtitle))
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.add_employee_event), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = companyName, onValueChange = { companyName = it }, label = { Text(stringResource(R.string.company_name)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = employeeName, onValueChange = { employeeName = it }, label = { Text(stringResource(R.string.employee_name)) }, modifier = Modifier.fillMaxWidth())
                    ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = !typeExpanded }) {
                        OutlinedTextField(
                            value = stringResource(eventType.labelRes),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.event_type)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                            CompanyEventType.entries.forEach { option ->
                                DropdownMenuItem(text = { Text(stringResource(option.labelRes)) }, onClick = { eventType = option; typeExpanded = false })
                            }
                        }
                    }
                    OutlinedTextField(value = dateText, onValueChange = { dateText = it }, label = { Text(stringResource(R.string.date_yyyy_mm_dd)) }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = {
                        val date = runCatching { LocalDate.parse(dateText.trim()) }.getOrNull()
                        if (companyName.isBlank() || employeeName.isBlank() || date == null) {
                            message = invalidCompanyEventError
                        } else {
                            FakeRepository.addCompanyEvent(employeeName.trim(), eventType, date, companyName.trim())
                            employeeName = ""; dateText = ""
                            message = employeeEventAdded
                        }
                    }) { Text(stringResource(R.string.add_event)) }
                }
            }
        }

        item { Text(stringResource(R.string.employee_events), style = MaterialTheme.typography.titleLarge) }
        items(FakeRepository.companyEvents, key = { it.id }) { event ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${event.employeeName} — ${stringResource(event.type.labelRes)}", style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.company_label, event.companyName))
                    Text(stringResource(R.string.date_label, event.date.toString()))
                }
            }
        }

        item { Text(stringResource(R.string.order_gift_company_event), style = MaterialTheme.typography.titleLarge) }
        items(FakeRepository.gifts, key = { "cg-${it.id}" }) { gift ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp)) {
                    RadioButton(selected = selectedGift?.id == gift.id, onClick = { selectedGift = gift })
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(gift.name, style = MaterialTheme.typography.titleMedium)
                        Text("$${"%.2f".format(gift.price)}")
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = recipientName, onValueChange = { recipientName = it }, label = { Text(stringResource(R.string.recipient_employee)) }, modifier = Modifier.fillMaxWidth())
                    message?.let { msg ->
                        val text = if (msg.startsWith("OK:")) stringResource(R.string.company_order_placed, msg.removePrefix("OK:")) else msg
                        Text(text, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(onClick = {
                        val gift = selectedGift
                        if (gift == null || recipientName.isBlank() || companyName.isBlank()) {
                            message = companyOrderError
                        } else {
                            val order = FakeRepository.placeCompanyOrder(companyName.trim(), gift, recipientName.trim())
                            message = "OK:$${"%.2f".format(order.total)}"
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.place_company_order)) }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
