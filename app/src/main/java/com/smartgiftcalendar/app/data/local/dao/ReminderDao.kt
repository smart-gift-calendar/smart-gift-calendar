package com.smartgiftcalendar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smartgiftcalendar.app.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow

/** Data access for scheduled [Reminder] rows. */
@Dao
interface ReminderDao {

    @Insert
    suspend fun insert(reminder: Reminder): Long

    @Insert
    suspend fun insertAll(reminders: List<Reminder>): List<Long>

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("SELECT * FROM reminders ORDER BY send_at ASC")
    fun observeAll(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE person_id = :personId ORDER BY send_at ASC")
    fun observeByPerson(personId: Long): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): Reminder?

    @Query("SELECT * FROM reminders WHERE sent = 0 AND send_at <= :now ORDER BY send_at ASC")
    suspend fun getDue(now: Long): List<Reminder>

    @Query("SELECT * FROM reminders WHERE sent = 0 AND send_at <= :now ORDER BY send_at ASC")
    fun observeDue(now: Long): Flow<List<Reminder>>

    @Query("UPDATE reminders SET sent = 1 WHERE id = :id")
    suspend fun markSent(id: Long)

    @Query("DELETE FROM reminders WHERE person_id = :personId")
    suspend fun deleteByPerson(personId: Long)
}
