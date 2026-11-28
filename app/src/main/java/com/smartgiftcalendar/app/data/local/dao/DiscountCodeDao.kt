package com.smartgiftcalendar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smartgiftcalendar.app.data.local.entity.DiscountCode
import kotlinx.coroutines.flow.Flow

/** Data access for [DiscountCode] rows. */
@Dao
interface DiscountCodeDao {

    @Insert
    suspend fun insert(discountCode: DiscountCode): Long

    @Insert
    suspend fun insertAll(discountCodes: List<DiscountCode>): List<Long>

    @Update
    suspend fun update(discountCode: DiscountCode)

    @Delete
    suspend fun delete(discountCode: DiscountCode)

    @Query("SELECT * FROM discount_codes ORDER BY percent DESC")
    fun observeAll(): Flow<List<DiscountCode>>

    @Query("SELECT * FROM discount_codes WHERE owner_id = :ownerId ORDER BY percent DESC")
    fun observeByOwner(ownerId: Long): Flow<List<DiscountCode>>

    @Query("SELECT * FROM discount_codes WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): DiscountCode?

    @Query("SELECT * FROM discount_codes WHERE id = :id")
    suspend fun getById(id: Long): DiscountCode?

    @Query("UPDATE discount_codes SET used_count = used_count + 1 WHERE id = :id")
    suspend fun incrementUsedCount(id: Long)

    @Query("SELECT COUNT(*) FROM discount_codes")
    suspend fun count(): Int
}
