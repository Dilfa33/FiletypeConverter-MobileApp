package com.example.project.repository.mappers

import com.example.project.data.local.entity.FileEntity
import com.example.project.model.ConversionStatus
import com.example.project.model.FileItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileMapper {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    fun toDomain(entity: FileEntity): FileItem = FileItem(
        id             = entity.id,
        name           = entity.name,
        originalFormat = entity.originalFormat,
        targetFormat   = entity.targetFormat,
        sizeMb         = entity.sizeMb,
        date           = dateFormat.format(Date(entity.convertedAt)),
        status         = ConversionStatus.valueOf(entity.status),
        outputPath     = entity.outputPath
    )

    fun toEntity(domain: FileItem, userId: String): FileEntity = FileEntity(
        id             = domain.id,
        name           = domain.name,
        originalFormat = domain.originalFormat,
        targetFormat   = domain.targetFormat,
        sizeMb         = domain.sizeMb,
        convertedAt    = System.currentTimeMillis(),
        status         = domain.status.name,
        userId         = userId,
        outputPath     = domain.outputPath
    )
}
