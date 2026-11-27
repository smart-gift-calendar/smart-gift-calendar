package com.smartgiftcalendar.app.data.model

import com.smartgiftcalendar.app.R
import java.time.LocalDate
import java.util.UUID

enum class BirthdayRelation(val labelRes: Int) {
    SELF(R.string.relation_self),
    FAMILY(R.string.relation_family),
    FRIEND(R.string.relation_friend)
}

enum class CompanyEventType(val labelRes: Int) {
    BIRTHDAY(R.string.event_birthday),
    ANNIVERSARY(R.string.event_anniversary)
}

data class BirthdayEntry(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val relation: BirthdayRelation,
    val date: LocalDate,
    val note: String = ""
)

data class Gift(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val price: Double
)

enum class OrderStatus(val labelRes: Int) {
    PENDING(R.string.status_pending),
    PROCESSING(R.string.status_processing),
    DELIVERED(R.string.status_delivered)
}

data class Order(
    val id: String = UUID.randomUUID().toString(),
    val customerName: String,
    val gift: Gift,
    val recipientName: String,
    val discountPercent: Int = 0,
    val createdAt: LocalDate = LocalDate.now(),
    val status: OrderStatus = OrderStatus.PENDING
) {
    val subtotal: Double get() = gift.price
    val discountAmount: Double get() = subtotal * discountPercent / 100.0
    val total: Double get() = subtotal - discountAmount
}

data class DiscountCode(
    val code: String,
    val percent: Int,
    val createdBy: String = "Admin",
    val active: Boolean = true,
    val expiryDate: LocalDate = LocalDate.now().plusMonths(3)
)

data class CompanyEvent(
    val id: String = UUID.randomUUID().toString(),
    val employeeName: String,
    val type: CompanyEventType,
    val date: LocalDate,
    val companyName: String
)

data class UserProfile(
    val name: String,
    val inviteCode: String,
    val joinedWithInviteCode: String? = null,
    val hasInviteDiscount: Boolean = false
)
