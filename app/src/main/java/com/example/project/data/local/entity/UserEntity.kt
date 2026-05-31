package com.example.project.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val passwordHash: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val photoUrl: String? = null
)
