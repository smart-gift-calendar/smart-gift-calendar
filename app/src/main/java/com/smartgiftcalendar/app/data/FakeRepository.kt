package com.smartgiftcalendar.app.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.smartgiftcalendar.app.data.model.BirthdayEntry
import com.smartgiftcalendar.app.data.model.BirthdayRelation
import com.smartgiftcalendar.app.data.model.CompanyEvent
import com.smartgiftcalendar.app.data.model.CompanyEventType
import com.smartgiftcalendar.app.data.model.DiscountCode
import com.smartgiftcalendar.app.data.model.Gift
import com.smartgiftcalendar.app.data.model.Order
import com.smartgiftcalendar.app.data.model.OrderStatus
import com.smartgiftcalendar.app.data.model.UserProfile
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object FakeRepository {
    val currentUser = mutableStateOf(
        UserProfile(name = "Alex", inviteCode = "ALEX2026", joinedWithInviteCode = null)
    )

    val birthdays = mutableStateListOf(
        BirthdayEntry(name = "Alex", relation = BirthdayRelation.SELF, date = LocalDate.now().plusDays(3), note = "My birthday"),
        BirthdayEntry(name = "Mom", relation = BirthdayRelation.FAMILY, date = LocalDate.now().plusDays(10), note = "Loves flowers"),
        BirthdayEntry(name = "Sam", relation = BirthdayRelation.FRIEND, date = LocalDate.now().plusDays(1), note = "Coffee fan"),
        BirthdayEntry(name = "Lina", relation = BirthdayRelation.FRIEND, date = LocalDate.now().plusDays(25), note = "Likes books")
    )

    val gifts = mutableStateListOf(
        Gift(name = "Flower Bouquet", description = "Fresh seasonal flowers", price = 35.0),
        Gift(name = "Chocolate Box", description = "Premium assorted chocolates", price = 24.0),
        Gift(name = "Smart Watch", description = "Fitness and notifications", price = 120.0),
        Gift(name = "Book Gift Set", description = "Bestseller bundle", price = 45.0)
    )

    val orders = mutableStateListOf(
        Order(customerName = "Alex", gift = gifts[0], recipientName = "Mom", discountPercent = 0, createdAt = LocalDate.now().minusDays(2))
    )

    val discountCodes = mutableStateListOf(
        DiscountCode(code = "FRIEND10", percent = 10, createdBy = "System"),
        DiscountCode(code = "WELCOME5", percent = 5, createdBy = "Admin")
    )

    val companyEvents = mutableStateListOf(
        CompanyEvent(employeeName = "Nora Smith", type = CompanyEventType.BIRTHDAY, date = LocalDate.now().plusDays(7), companyName = "Acme Inc"),
        CompanyEvent(employeeName = "James Lee", type = CompanyEventType.ANNIVERSARY, date = LocalDate.now().plusDays(14), companyName = "Acme Inc")
    )

    val inviteRegistrations = mutableStateListOf<String>()

    fun addBirthday(name: String, relation: BirthdayRelation, date: LocalDate, note: String) {
        birthdays.add(BirthdayEntry(name = name, relation = relation, date = date, note = note))
    }

    fun upcomingBirthdays(): List<BirthdayEntry> {
        return birthdays.sortedBy { daysUntil(it.date) }
    }

    fun daysUntil(date: LocalDate): Long {
        val today = LocalDate.now()
        val next = date.withYear(today.year).let { if (it.isBefore(today)) it.plusYears(1) else it }
        return ChronoUnit.DAYS.between(today, next)
    }

    fun nextOccurrence(date: LocalDate): LocalDate {
        val today = LocalDate.now()
        val next = date.withYear(today.year)
        return if (next.isBefore(today)) next.plusYears(1) else next
    }

    fun remindersForToday(): List<Pair<BirthdayEntry, String>> {
        return birthdays.mapNotNull { entry ->
            when (daysUntil(entry.date)) {
                0L -> entry to "Today is ${entry.name}'s birthday!"
                3L -> entry to "Reminder: ${entry.name}'s birthday is in 3 days."
                else -> null
            }
        }
    }

    fun registerThroughInvite(friendName: String, inviteCode: String, birthday: LocalDate): Boolean {
        val normalized = inviteCode.trim().uppercase()
        val valid = normalized.isNotBlank() && normalized == currentUser.value.inviteCode.uppercase()
        if (valid) {
            inviteRegistrations.add("$friendName joined with $normalized")
            currentUser.value = currentUser.value.copy(hasInviteDiscount = true)
            if (discountCodes.none { it.code == "FRIEND10" }) {
                discountCodes.add(DiscountCode(code = "FRIEND10", percent = 10, createdBy = "Invite"))
            }
            birthdays.add(BirthdayEntry(name = friendName, relation = BirthdayRelation.FRIEND, date = birthday))
        }
        return valid
    }

    fun availableDiscountForUser(): DiscountCode? {
        return if (currentUser.value.hasInviteDiscount) discountCodes.firstOrNull { it.code == "FRIEND10" && it.active } else null
    }

    fun placeOrder(customerName: String, gift: Gift, recipientName: String, discountCode: String?): Order {
        val discount = discountCode?.trim()?.uppercase()?.takeIf { it.isNotEmpty() }
            ?.let { code -> discountCodes.firstOrNull { it.code.uppercase() == code && it.active } }
        val order = Order(
            customerName = customerName,
            gift = gift,
            recipientName = recipientName,
            discountPercent = discount?.percent ?: 0
        )
        orders.add(0, order)
        return order
    }

    fun addGift(name: String, description: String, price: Double) {
        gifts.add(Gift(name = name, description = description, price = price))
    }

    // Update an existing gift by id.
    fun updateGift(id: String, name: String, description: String, price: Double) {
        val index = gifts.indexOfFirst { it.id == id }
        if (index >= 0) {
            gifts[index] = gifts[index].copy(name = name, description = description, price = price)
        }
    }

    // Delete a gift from the fake list.
    fun deleteGift(id: String) {
        gifts.removeAll { it.id == id }
    }

    fun addDiscountCode(code: String, percent: Int, expiryDate: LocalDate) {
        val normalized = code.trim().uppercase()
        if (normalized.isNotEmpty() && discountCodes.none { it.code == normalized }) {
            discountCodes.add(
                DiscountCode(code = normalized, percent = percent, createdBy = "Admin", expiryDate = expiryDate)
            )
        }
    }

    // Change order status from the admin panel.
    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        val index = orders.indexOfFirst { it.id == orderId }
        if (index >= 0) {
            orders[index] = orders[index].copy(status = status)
        }
    }

    fun addCompanyEvent(employeeName: String, type: CompanyEventType, date: LocalDate, companyName: String) {
        companyEvents.add(CompanyEvent(employeeName = employeeName, type = type, date = date, companyName = companyName))
    }

    fun placeCompanyOrder(companyName: String, gift: Gift, recipientName: String): Order {
        val order = Order(customerName = companyName, gift = gift, recipientName = recipientName)
        orders.add(0, order)
        return order
    }
}
