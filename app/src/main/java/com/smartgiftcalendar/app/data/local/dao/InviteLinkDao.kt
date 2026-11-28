package com.smartgiftcalendar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smartgiftcalendar.app.data.local.entity.InviteLink
import kotlinx.coroutines.flow.Flow

/** Data access for [InviteLink] rows. */
@Dao
interface InviteLinkDao {

    @Insert
    suspend fun insert(inviteLink: InviteLink): Long

    @Update
    suspend fun update(inviteLink: InviteLink)

    @Delete
    suspend fun delete(inviteLink: InviteLink)

    @Query("SELECT * FROM invite_links ORDER BY created_at DESC")
    fun observeAll(): Flow<List<InviteLink>>

    @Query("SELECT * FROM invite_links WHERE owner_id = :ownerId ORDER BY created_at DESC")
    fun observeByOwner(ownerId: Long): Flow<List<InviteLink>>

    @Query("SELECT * FROM invite_links WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): InviteLink?

    @Query("SELECT * FROM invite_links WHERE id = :id")
    suspend fun getById(id: Long): InviteLink?

    @Query("SELECT * FROM invite_links WHERE owner_id = :ownerId LIMIT 1")
    suspend fun getByOwner(ownerId: Long): InviteLink?
}
