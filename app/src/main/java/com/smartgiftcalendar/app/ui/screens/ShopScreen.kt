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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartgiftcalendar.app.data.FakeRepository
import com.smartgiftcalendar.app.data.model.Gift

@Composable
fun ShopScreen() {
    var selectedGift by remember { mutableStateOf<Gift?>(FakeRepository.gifts.firstOrNull()) }
    var customerName by remember { mutableStateOf(FakeRepository.currentUser.value.name) }
    var recipientName by remember { mutableStateOf("") }
    var discountCode by remember { mutableStateOf(FakeRepository.availableDiscountForUser()?.code ?: "") }
    var message by remember { mutableStateOf<String?>(null) }

    val discount = discountCode.trim().uppercase().takeIf { it.isNotEmpty() }
        ?.let { code -> FakeRepository.discountCodes.firstOrNull { it.code.equals(code, true) && it.active } }
    val estimate = selectedGift?.price ?: 0.0
    val discountAmount = estimate * (discount?.percent ?: 0) / 100.0
    val total = estimate - discountAmount

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Gift Shop", style = MaterialTheme.typography.headlineMedium)
            Text("Choose a gift, estimate price, and place an order.", style = MaterialTheme.typography.bodyMedium)
            FakeRepository.availableDiscountForUser()?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Invite discount available: ${it.code} (${it.percent}% off)")
            }
        }

        items(FakeRepository.gifts, key = { it.id }) { gift ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp)) {
                    RadioButton(selected = selectedGift?.id == gift.id, onClick = { selectedGift = gift })
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(gift.name, style = MaterialTheme.typography.titleMedium)
                        Text(gift.description)
                        Text("$${"%.2f".format(gift.price)}")
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Order details", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Your name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = recipientName, onValueChange = { recipientName = it }, label = { Text("Recipient") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = discountCode, onValueChange = { discountCode = it }, label = { Text("Discount code") }, modifier = Modifier.fillMaxWidth())
                    Text("Price estimate", style = MaterialTheme.typography.titleSmall)
                    Text("Subtotal: $${"%.2f".format(estimate)}")
                    Text("Discount: ${discount?.percent ?: 0}% (-$${"%.2f".format(discountAmount)})")
                    Text("Total: $${"%.2f".format(total)}", style = MaterialTheme.typography.titleMedium)
                    message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                    Button(
                        onClick = {
                            val gift = selectedGift
                            when {
                                gift == null -> message = "Select a gift."
                                customerName.isBlank() || recipientName.isBlank() -> message = "Enter your name and recipient."
                                else -> {
                                    val order = FakeRepository.placeOrder(customerName.trim(), gift, recipientName.trim(), discountCode)
                                    message = "Order placed! Total: $${"%.2f".format(order.total)}"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Place order") }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
