package com.smartgiftcalendar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** A scheduled reminder for a person's event. [type] is `THREE_DAYS` or `SAME_DAY`. */
@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Person::class,
            parentColumns = ["id"],
            childColumns = ["person_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["person_id"])]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "person_id")
    val personId: Long,
    /** Epoch millis at which the reminder should be sent. */
    @ColumnInfo(name = "send_at")
    val sendAt: Long,
    val type: String,
    val sent: Boolean = false
)
