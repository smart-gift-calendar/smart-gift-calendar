package com.smartgiftcalendar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** A registered account. The role decides which panel the user can open. */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    val salt: String,
    val role: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
