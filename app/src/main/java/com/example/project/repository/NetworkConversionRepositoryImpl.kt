package com.example.project.repository

import com.example.project.data.remote.api.FileCastApiService
import com.example.project.data.remote.dto.ConversionDto
import com.example.project.data.remote.dto.CreateConversionDto
import com.example.project.data.remote.dto.UpdateConversionDto
import javax.inject.Inject

class NetworkConversionRepositoryImpl @Inject constructor(
    private val api: FileCastApiService
) : NetworkConversionRepository {

    override suspend fun getConversions(): List<ConversionDto> = api.getConversions()

    override suspend fun getConversionById(id: String): ConversionDto = api.getConversionById(id)

    override suspend fun createConversion(dto: CreateConversionDto): ConversionDto =
        api.createConversion(dto)

    override suspend fun updateConversion(id: String, dto: UpdateConversionDto): ConversionDto =
        api.updateConversion(id, dto)

    override suspend fun deleteConversion(id: String) = api.deleteConversion(id)
}
