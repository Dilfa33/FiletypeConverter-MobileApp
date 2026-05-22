package com.example.project.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

// One-to-Many: one User has many Files
data class UserWithFiles(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val files: List<FileEntity>
)
