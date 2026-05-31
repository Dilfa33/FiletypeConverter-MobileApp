package com.example.project.data.remote.dto

data class ConversionDto(
    val id: String,
    val name: String,
    val originalFormat: String,
    val targetFormat: String,
    val sizeMb: Float,
    val date: String,
    val status: String
)

data class CreateConversionDto(
    val name: String,
    val originalFormat: String,
    val targetFormat: String,
    val sizeMb: Float,
    val date: String,
    val status: String = "SUCCESS"
)

data class UpdateConversionDto(
    val name: String? = null,
    val originalFormat: String? = null,
    val targetFormat: String? = null,
    val sizeMb: Float? = null,
    val date: String? = null,
    val status: String? = null
)

/** Response from POST /convert/ — download URL for the CloudConvert output file. */
data class ConvertResponseDto(
    val downloadUrl: String,
    val fileName: String
)
