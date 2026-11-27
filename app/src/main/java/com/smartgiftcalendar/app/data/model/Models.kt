package com.smartgiftcalendar.app.data.model

import java.time.LocalDate
import java.util.UUID

enum class BirthdayRelation(val label: String) {
    SELF("Self"),
    FAMILY("Family"),
    FRIEND("Friend")
}

enum class CompanyEventType(val label: String) {
    BIRTHDAY("Birthday"),
    ANNIVERSARY("Anniversary")
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

enum class OrderStatus(val label: String) {
    PENDING("Pending"),
    PROCESSING("Processing"),
    DELIVERED("Delivered")
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
