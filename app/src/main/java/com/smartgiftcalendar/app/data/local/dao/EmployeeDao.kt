package com.smartgiftcalendar.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smartgiftcalendar.app.data.local.entity.Employee
import kotlinx.coroutines.flow.Flow

/** Data access for [Employee] rows belonging to an organization. */
@Dao
interface EmployeeDao {

    @Insert
    suspend fun insert(employee: Employee): Long

    @Insert
    suspend fun insertAll(employees: List<Employee>): List<Long>

    @Update
    suspend fun update(employee: Employee)

    @Delete
    suspend fun delete(employee: Employee)

    @Query("SELECT * FROM employees WHERE organization_id = :organizationId ORDER BY name ASC")
    fun observeByOrganization(organizationId: Long): Flow<List<Employee>>

    @Query("SELECT * FROM employees ORDER BY name ASC")
    fun observeAll(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getById(id: Long): Employee?

    @Query("SELECT * FROM employees WHERE organization_id = :organizationId ORDER BY birth_date ASC")
    suspend fun getByOrganization(organizationId: Long): List<Employee>

    @Query("DELETE FROM employees WHERE organization_id = :organizationId")
    suspend fun deleteByOrganization(organizationId: Long)
}
