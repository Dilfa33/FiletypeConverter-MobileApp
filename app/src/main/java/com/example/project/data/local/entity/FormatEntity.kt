package com.example.project.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "formats")
data class FormatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,       // "PDF", "DOCX", "MP3" …
    val extension: String,  // ".pdf", ".docx", ".mp3" …
    val category: String    // "document" | "image" | "audio" | "video"
)
