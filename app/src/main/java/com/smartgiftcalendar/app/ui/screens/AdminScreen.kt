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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartgiftcalendar.app.data.FakeRepository
import com.smartgiftcalendar.app.data.model.Gift
import com.smartgiftcalendar.app.data.model.OrderStatus
import java.time.LocalDate

@Composable
fun AdminScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Orders", "Gifts", "Discount codes")

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Admin Panel",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        // Simple tabs for admin sections.
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
            }
        }

        when (selectedTab) {
            0 -> OrdersTab()
            1 -> GiftsTab()
            2 -> DiscountCodesTab()
        }
    }
}

@Composable
private fun OrdersTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("Orders", style = MaterialTheme.typography.titleLarge) }
        items(FakeRepository.orders, key = { it.id }) { order ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${order.gift.name} for ${order.recipientName}", style = MaterialTheme.typography.titleMedium)
                    Text("Customer: ${order.customerName}")
                    Text("Final price: $${"%.2f".format(order.total)}")
                    Text("Status: ${order.status.label}")
                    // Change status with a simple button.
                    Button(onClick = {
                        val next = when (order.status) {
                            OrderStatus.PENDING -> OrderStatus.PROCESSING
                            OrderStatus.PROCESSING -> OrderStatus.DELIVERED
                            OrderStatus.DELIVERED -> OrderStatus.PENDING
                        }
                        FakeRepository.updateOrderStatus(order.id, next)
                    }) { Text("Change status") }
                }
            }
        }
    }
}

@Composable
private fun GiftsTab() {
    var editingGift by remember { mutableStateOf<Gift?>(null) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    // Fill form when editing a gift.
    fun startEdit(gift: Gift) {
        editingGift = gift
        name = gift.name
        description = gift.description
        priceText = gift.price.toString()
        message = null
    }

    fun clearForm() {
        editingGift = null
        name = ""; description = ""; priceText = ""; message = null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (editingGift == null) "Add gift" else "Edit gift", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Gift name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = priceText, onValueChange = { priceText = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
                    message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            val price = priceText.toDoubleOrNull()
                            if (name.isBlank() || description.isBlank() || price == null || price <= 0) {
                                message = "Enter a valid name, description, and price."
                            } else {
                                val current = editingGift
                                if (current == null) {
                                    FakeRepository.addGift(name.trim(), description.trim(), price)
                                    message = "Gift added."
                                } else {
                                    FakeRepository.updateGift(current.id, name.trim(), description.trim(), price)
                                    message = "Gift updated."
                                }
                                clearForm()
                            }
                        }) { Text(if (editingGift == null) "Add gift" else "Save changes") }
                        if (editingGift != null) {
                            TextButton(onClick = { clearForm() }) { Text("Cancel") }
                        }
                    }
                }
            }
        }

        item { Text("Gifts", style = MaterialTheme.typography.titleLarge) }
        items(FakeRepository.gifts, key = { it.id }) { gift ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(gift.name, style = MaterialTheme.typography.titleMedium)
                    Text(gift.description)
                    Text("$${"%.2f".format(gift.price)}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { startEdit(gift) }) { Text("Edit") }
                        // Delete gift from fake repository.
                        TextButton(onClick = { FakeRepository.deleteGift(gift.id) }) { Text("Delete") }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun DiscountCodesTab() {
    var code by remember { mutableStateOf("") }
    var percentText by remember { mutableStateOf("") }
    var expiryText by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Create discount code", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Code") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = percentText, onValueChange = { percentText = it }, label = { Text("Percent") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = expiryText, onValueChange = { expiryText = it }, label = { Text("Expiry date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                    Button(onClick = {
                        val percent = percentText.toIntOrNull()
                        val expiry = runCatching { LocalDate.parse(expiryText.trim()) }.getOrNull()
                        if (code.isBlank() || percent == null || percent !in 1..100 || expiry == null) {
                            message = "Enter a valid code, percent (1-100), and expiry date."
                        } else {
                            FakeRepository.addDiscountCode(code, percent, expiry)
                            code = ""; percentText = ""; expiryText = ""
                            message = "Discount code created."
                        }
                    }) { Text("Create code") }
                }
            }
        }

        item { Text("Discount codes", style = MaterialTheme.typography.titleLarge) }
        items(FakeRepository.discountCodes, key = { it.code }) { discount ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${discount.code} — ${discount.percent}%", style = MaterialTheme.typography.titleMedium)
                    Text("Expiry date: ${discount.expiryDate}")
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
