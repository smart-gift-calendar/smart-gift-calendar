package com.smartgiftcalendar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** A user's personal invite link code. */
@Entity(
    tableName = "invite_links",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["owner_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["code"], unique = true),
        Index(value = ["owner_id"])
    ]
)
data class InviteLink(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    @ColumnInfo(name = "owner_id")
    val ownerId: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
