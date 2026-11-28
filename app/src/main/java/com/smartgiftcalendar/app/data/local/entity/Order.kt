package com.smartgiftcalendar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** A placed gift order. [status] is the order status as a string. */
@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["person_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Gift::class,
            parentColumns = ["id"],
            childColumns = ["gift_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = DiscountCode::class,
            parentColumns = ["id"],
            childColumns = ["discount_code_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["person_id"]),
        Index(value = ["gift_id"]),
        Index(value = ["discount_code_id"])
    ]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "person_id")
    val personId: Long? = null,
    @ColumnInfo(name = "gift_id")
    val giftId: Long,
    @ColumnInfo(name = "discount_code_id")
    val discountCodeId: Long? = null,
    /** Final price in toman, after the discount. */
    @ColumnInfo(name = "final_price")
    val finalPrice: Long,
    val status: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
