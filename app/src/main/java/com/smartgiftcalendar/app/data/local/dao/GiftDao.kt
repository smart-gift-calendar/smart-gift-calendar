package com.smartgiftcalendar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smartgiftcalendar.app.data.local.entity.Gift
import kotlinx.coroutines.flow.Flow

/** Data access for the [Gift] catalogue. */
@Dao
interface GiftDao {

    @Insert
    suspend fun insert(gift: Gift): Long

    @Insert
    suspend fun insertAll(gifts: List<Gift>): List<Long>

    @Update
    suspend fun update(gift: Gift)

    @Delete
    suspend fun delete(gift: Gift)

    @Query("SELECT * FROM gifts ORDER BY price ASC")
    fun observeAll(): Flow<List<Gift>>

    @Query("SELECT * FROM gifts WHERE active = 1 ORDER BY price ASC")
    fun observeActive(): Flow<List<Gift>>

    @Query("SELECT * FROM gifts WHERE category = :category AND active = 1 ORDER BY price ASC")
    fun observeByCategory(category: String): Flow<List<Gift>>

    @Query("SELECT * FROM gifts WHERE active = 1 AND name LIKE '%' || :query || '%' ORDER BY price ASC")
    fun search(query: String): Flow<List<Gift>>

    @Query("SELECT * FROM gifts WHERE id = :id")
    suspend fun getById(id: Long): Gift?

    @Query("SELECT COUNT(*) FROM gifts")
    suspend fun count(): Int
}
