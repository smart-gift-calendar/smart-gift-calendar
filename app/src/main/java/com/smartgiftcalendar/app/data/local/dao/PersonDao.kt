package com.smartgiftcalendar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smartgiftcalendar.app.data.local.entity.Person
import kotlinx.coroutines.flow.Flow

/** Data access for [Person] calendar entries. */
@Dao
interface PersonDao {

    @Insert
    suspend fun insert(person: Person): Long

    @Insert
    suspend fun insertAll(persons: List<Person>): List<Long>

    @Update
    suspend fun update(person: Person)

    @Delete
    suspend fun delete(person: Person)

    @Query("SELECT * FROM persons WHERE owner_id = :ownerId ORDER BY event_date ASC")
    fun observeByOwner(ownerId: Long): Flow<List<Person>>

    @Query("SELECT * FROM persons WHERE owner_id = :ownerId AND event_type = :eventType ORDER BY event_date ASC")
    fun observeByOwnerAndEventType(ownerId: Long, eventType: String): Flow<List<Person>>

    @Query("SELECT * FROM persons ORDER BY event_date ASC")
    fun observeAll(): Flow<List<Person>>

    @Query("SELECT * FROM persons WHERE id = :id")
    suspend fun getById(id: Long): Person?

    @Query("SELECT * FROM persons WHERE owner_id = :ownerId AND event_date = :eventDate")
    suspend fun getByOwnerAndDate(ownerId: Long, eventDate: String): List<Person>

    @Query("DELETE FROM persons WHERE owner_id = :ownerId")
    suspend fun deleteByOwner(ownerId: Long)
}
