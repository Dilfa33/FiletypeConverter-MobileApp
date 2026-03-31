package com.example.project.model

data class FileItem(
    val id: String,
    val name: String,
    val originalFormat: String,
    val targetFormat: String,
    val sizeMb: Float,
    val date: String,
    val status: ConversionStatus
)

enum class ConversionStatus {
    SUCCESS, FAILED, PROCESSING
}
