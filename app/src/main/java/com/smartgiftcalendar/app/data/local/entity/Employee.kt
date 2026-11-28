package com.smartgiftcalendar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * An employee of an organization. [birthDate] and [marriageDate] are ISO `yyyy-MM-dd` strings;
 * [marriageDate] is null when the employee has no marriage anniversary on record.
 */
@Entity(
    tableName = "employees",
    foreignKeys = [
        ForeignKey(
            entity = Organization::class,
            parentColumns = ["id"],
            childColumns = ["organization_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["organization_id"])]
)
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "organization_id")
    val organizationId: Long,
    val name: String,
    /** ISO date in `yyyy-MM-dd` form. */
    @ColumnInfo(name = "birth_date")
    val birthDate: String,
    /** ISO date in `yyyy-MM-dd` form, or null when unknown. */
    @ColumnInfo(name = "marriage_date")
    val marriageDate: String? = null
)
