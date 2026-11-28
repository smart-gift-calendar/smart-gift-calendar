package com.smartgiftcalendar.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** A gift in the shop catalogue. [price] is stored in toman. */
@Entity(tableName = "gifts")
data class Gift(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    /** Price in toman. */
    val price: Long,
    @ColumnInfo(name = "image_url")
    val imageUrl: String = "",
    val category: String = "",
    val active: Boolean = true
)
