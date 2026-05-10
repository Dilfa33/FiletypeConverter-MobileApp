package com.example.project.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "conversion_jobs",
    foreignKeys = [
        ForeignKey(
            entity = FileEntity::class,
            parentColumns = ["id"],
            childColumns = ["fileId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ConversionJobEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(index = true) val fileId: String,
    val status: String,           // "QUEUED" | "RUNNING" | "DONE" | "FAILED"
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
