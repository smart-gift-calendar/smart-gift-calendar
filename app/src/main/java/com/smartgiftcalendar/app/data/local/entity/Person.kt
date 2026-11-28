package com.smartgiftcalendar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** A calendar entry owned by a user. [eventDate] is an ISO `yyyy-MM-dd` string. */
@Entity(
    tableName = "persons",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["owner_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["owner_id"])]
)
data class Person(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "owner_id")
    val ownerId: Long,
    val name: String,
    val relation: String,
    @ColumnInfo(name = "event_type")
    val eventType: String,
    /** ISO date in `yyyy-MM-dd` form. */
    @ColumnInfo(name = "event_date")
    val eventDate: String,
    val note: String = ""
)
