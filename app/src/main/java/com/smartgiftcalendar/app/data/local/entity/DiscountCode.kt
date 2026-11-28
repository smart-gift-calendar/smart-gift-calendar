package com.smartgiftcalendar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A percentage discount code. [ownerId] is set when the code belongs to a specific user
 * (for example the 10% code a guest receives through an invite link).
 * [expiresAt] is an ISO `yyyy-MM-dd` string.
 */
@Entity(
    tableName = "discount_codes",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["owner_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["code"], unique = true),
        Index(value = ["owner_id"])
    ]
)
data class DiscountCode(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val percent: Int,
    /** ISO date in `yyyy-MM-dd` form. */
    @ColumnInfo(name = "expires_at")
    val expiresAt: String,
    @ColumnInfo(name = "max_uses")
    val maxUses: Int,
    @ColumnInfo(name = "used_count")
    val usedCount: Int = 0,
    @ColumnInfo(name = "owner_id")
    val ownerId: Long? = null
)
