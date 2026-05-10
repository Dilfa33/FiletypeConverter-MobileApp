package com.example.project.data.local.dao

import androidx.room.*
import com.example.project.data.local.entity.UserEntity
import com.example.project.data.local.entity.UserWithFiles
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Transaction
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserWithFiles(id: String): UserWithFiles?
}
