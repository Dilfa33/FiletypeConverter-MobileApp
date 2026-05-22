package com.example.project.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_tags")
data class FileTagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
