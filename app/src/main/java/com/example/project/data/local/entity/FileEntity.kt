package com.example.project.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "files",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val originalFormat: String,
    val targetFormat: String,
    val sizeMb: Float,
    val convertedAt: Long,
    val status: String,           // "SUCCESS" | "FAILED" | "PROCESSING"
    @ColumnInfo(index = true) val userId: String,
    val outputPath: String? = null
)
