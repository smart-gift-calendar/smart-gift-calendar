package com.smartgiftcalendar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smartgiftcalendar.app.data.local.entity.User
import kotlinx.coroutines.flow.Flow

/** Data access for [User] rows. */
@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users ORDER BY created_at DESC")
    fun observeAll(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = :role ORDER BY name ASC")
    fun observeByRole(role: String): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Long): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): User?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}
