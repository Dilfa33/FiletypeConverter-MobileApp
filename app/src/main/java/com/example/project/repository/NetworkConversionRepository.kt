package com.example.project.repository

import com.example.project.data.remote.dto.ConversionDto
import com.example.project.data.remote.dto.CreateConversionDto
import com.example.project.data.remote.dto.UpdateConversionDto

interface NetworkConversionRepository {
    suspend fun getConversions(): List<ConversionDto>
    suspend fun getConversionById(id: String): ConversionDto
    suspend fun createConversion(dto: CreateConversionDto): ConversionDto
    suspend fun updateConversion(id: String, dto: UpdateConversionDto): ConversionDto
    suspend fun deleteConversion(id: String)
}
