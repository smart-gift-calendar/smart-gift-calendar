package com.smartgiftcalendar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smartgiftcalendar.app.data.local.entity.Organization
import kotlinx.coroutines.flow.Flow

/** Data access for [Organization] rows. */
@Dao
interface OrganizationDao {

    @Insert
    suspend fun insert(organization: Organization): Long

    @Update
    suspend fun update(organization: Organization)

    @Delete
    suspend fun delete(organization: Organization)

    @Query("SELECT * FROM organizations ORDER BY name ASC")
    fun observeAll(): Flow<List<Organization>>

    @Query("SELECT * FROM organizations WHERE admin_user_id = :adminUserId ORDER BY name ASC")
    fun observeByAdmin(adminUserId: Long): Flow<List<Organization>>

    @Query("SELECT * FROM organizations WHERE id = :id")
    suspend fun getById(id: Long): Organization?

    @Query("SELECT * FROM organizations WHERE admin_user_id = :adminUserId LIMIT 1")
    suspend fun getByAdmin(adminUserId: Long): Organization?
}
