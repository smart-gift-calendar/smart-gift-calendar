package com.smartgiftcalendar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smartgiftcalendar.app.data.local.entity.Order
import kotlinx.coroutines.flow.Flow

/** Data access for placed [Order] rows. */
@Dao
interface OrderDao {

    @Insert
    suspend fun insert(order: Order): Long

    @Insert
    suspend fun insertAll(orders: List<Order>): List<Long>

    @Update
    suspend fun update(order: Order)

    @Delete
    suspend fun delete(order: Order)

    @Query("SELECT * FROM orders ORDER BY created_at DESC")
    fun observeAll(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY created_at DESC")
    fun observeByUser(userId: Long): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY created_at DESC")
    fun observeByStatus(status: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getById(id: Long): Order?

    @Query("SELECT * FROM orders WHERE user_id = :userId AND status = :status ORDER BY created_at DESC")
    suspend fun getByUserAndStatus(userId: Long, status: String): List<Order>

    @Query("UPDATE orders SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun count(): Int
}
