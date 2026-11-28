package com.smartgiftcalendar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** A company account managed by a user with the organization-manager role. */
@Entity(
    tableName = "organizations",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["admin_user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["admin_user_id"])]
)
data class Organization(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "admin_user_id")
    val adminUserId: Long
)
