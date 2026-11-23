package com.smartgiftcalendar.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartgiftcalendar.app.data.FakeRepository

@Composable
fun AdminScreen() {
    var giftName by remember { mutableStateOf("") }
    var giftDescription by remember { mutableStateOf("") }
    var giftPrice by remember { mutableStateOf("") }
    var discountCode by remember { mutableStateOf("") }
    var discountPercent by remember { mutableStateOf("") }
    var adminMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Admin Panel", style = MaterialTheme.typography.headlineMedium)
            Text("Manage orders, gifts, and discount codes.")
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Add gift", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = giftName, onValueChange = { giftName = it }, label = { Text("Gift name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = giftDescription, onValueChange = { giftDescription = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = giftPrice, onValueChange = { giftPrice = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = {
                        val price = giftPrice.toDoubleOrNull()
                        if (giftName.isBlank() || giftDescription.isBlank() || price == null || price <= 0) {
                            adminMessage = "Enter a valid gift name, description, and price."
                        } else {
                            FakeRepository.addGift(giftName.trim(), giftDescription.trim(), price)
                            giftName = ""; giftDescription = ""; giftPrice = ""
                            adminMessage = "Gift added."
                        }
                    }) { Text("Add gift") }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Create discount code", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = discountCode, onValueChange = { discountCode = it }, label = { Text("Code") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = discountPercent, onValueChange = { discountPercent = it }, label = { Text("Percent") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = {
                        val percent = discountPercent.toIntOrNull()
                        if (discountCode.isBlank() || percent == null || percent !in 1..100) {
                            adminMessage = "Enter a valid code and percent (1-100)."
                        } else {
                            FakeRepository.addDiscountCode(discountCode, percent)
                            discountCode = ""; discountPercent = ""
                            adminMessage = "Discount code created."
                        }
                    }) { Text("Create code") }
                    adminMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                }
            }
        }

        item { Text("Orders", style = MaterialTheme.typography.titleLarge) }
        items(FakeRepository.orders, key = { it.id }) { order ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${order.gift.name} for ${order.recipientName}", style = MaterialTheme.typography.titleMedium)
                    Text("Customer: ${order.customerName}")
                    Text("Subtotal: $${"%.2f".format(order.subtotal)}")
                    Text("Discount: ${order.discountPercent}% (-$${"%.2f".format(order.discountAmount)})")
                    Text("Total: $${"%.2f".format(order.total)}")
                    Text("Date: ${order.createdAt}")
                }
            }
        }

        item { Text("Discount codes", style = MaterialTheme.typography.titleLarge) }
        items(FakeRepository.discountCodes, key = { it.code }) { code ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${code.code} — ${code.percent}%", style = MaterialTheme.typography.titleMedium)
                    Text("Created by: ${code.createdBy}")
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
