package com.example.project.data.local.util

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Long? = value

    @TypeConverter
    fun toTimestamp(value: Long?): Long? = value

    @TypeConverter
    fun fromString(value: String?): String? = value

    @TypeConverter
    fun toString(value: String?): String? = value
}
