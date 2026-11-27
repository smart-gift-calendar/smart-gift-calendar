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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smartgiftcalendar.app.R
import com.smartgiftcalendar.app.data.FakeRepository
import com.smartgiftcalendar.app.data.model.Gift
import com.smartgiftcalendar.app.data.model.OrderStatus
import java.time.LocalDate

@Composable
fun AdminScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(stringResource(R.string.orders), stringResource(R.string.gifts), stringResource(R.string.discount_codes))

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            stringResource(R.string.admin_panel_title),
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
        item { Text(stringResource(R.string.orders), style = MaterialTheme.typography.titleLarge) }
        items(FakeRepository.orders, key = { it.id }) { order ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.gift_for_recipient, order.gift.name, order.recipientName), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.customer_label, order.customerName))
                    Text(stringResource(R.string.final_price, "$${"%.2f".format(order.total)}"))
                    Text(stringResource(R.string.status_label, stringResource(order.status.labelRes)))
                    // Change status with a simple button.
                    Button(onClick = {
                        val next = when (order.status) {
                            OrderStatus.PENDING -> OrderStatus.PROCESSING
                            OrderStatus.PROCESSING -> OrderStatus.DELIVERED
                            OrderStatus.DELIVERED -> OrderStatus.PENDING
                        }
                        FakeRepository.updateOrderStatus(order.id, next)
                    }) { Text(stringResource(R.string.change_status)) }
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

    val invalidGiftError = stringResource(R.string.invalid_gift_error)
    val giftAdded = stringResource(R.string.gift_added)
    val giftUpdated = stringResource(R.string.gift_updated)

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
                    Text(if (editingGift == null) stringResource(R.string.add_gift) else stringResource(R.string.edit_gift), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.gift_name)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(stringResource(R.string.description)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = priceText, onValueChange = { priceText = it }, label = { Text(stringResource(R.string.price)) }, modifier = Modifier.fillMaxWidth())
                    message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            val price = priceText.toDoubleOrNull()
                            if (name.isBlank() || description.isBlank() || price == null || price <= 0) {
                                message = invalidGiftError
                            } else {
                                val current = editingGift
                                if (current == null) {
                                    FakeRepository.addGift(name.trim(), description.trim(), price)
                                    message = giftAdded
                                } else {
                                    FakeRepository.updateGift(current.id, name.trim(), description.trim(), price)
                                    message = giftUpdated
                                }
                                clearForm()
                                // Restore message after clear.
                                message = if (current == null) giftAdded else giftUpdated
                            }
                        }) { Text(if (editingGift == null) stringResource(R.string.add_gift) else stringResource(R.string.save_changes)) }
                        if (editingGift != null) {
                            TextButton(onClick = { clearForm() }) { Text(stringResource(R.string.cancel)) }
                        }
                    }
                }
            }
        }

        item { Text(stringResource(R.string.gifts), style = MaterialTheme.typography.titleLarge) }
        items(FakeRepository.gifts, key = { it.id }) { gift ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(gift.name, style = MaterialTheme.typography.titleMedium)
                    Text(gift.description)
                    Text("$${"%.2f".format(gift.price)}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { startEdit(gift) }) { Text(stringResource(R.string.edit)) }
                        // Delete gift from fake repository.
                        TextButton(onClick = { FakeRepository.deleteGift(gift.id) }) { Text(stringResource(R.string.delete)) }
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

    val invalidDiscountError = stringResource(R.string.invalid_discount_error)
    val discountCreated = stringResource(R.string.discount_code_created)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.create_discount_code), style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text(stringResource(R.string.code)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = percentText, onValueChange = { percentText = it }, label = { Text(stringResource(R.string.percent)) }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = expiryText, onValueChange = { expiryText = it }, label = { Text(stringResource(R.string.expiry_date_yyyy_mm_dd)) }, modifier = Modifier.fillMaxWidth())
                    message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
                    Button(onClick = {
                        val percent = percentText.toIntOrNull()
                        val expiry = runCatching { LocalDate.parse(expiryText.trim()) }.getOrNull()
                        if (code.isBlank() || percent == null || percent !in 1..100 || expiry == null) {
                            message = invalidDiscountError
                        } else {
                            FakeRepository.addDiscountCode(code, percent, expiry)
                            code = ""; percentText = ""; expiryText = ""
                            message = discountCreated
                        }
                    }) { Text(stringResource(R.string.create_code)) }
                }
            }
        }

        item { Text(stringResource(R.string.discount_codes), style = MaterialTheme.typography.titleLarge) }
        items(FakeRepository.discountCodes, key = { it.code }) { discount ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.discount_code_item, discount.code, discount.percent), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.expiry_date_label, discount.expiryDate.toString()))
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
