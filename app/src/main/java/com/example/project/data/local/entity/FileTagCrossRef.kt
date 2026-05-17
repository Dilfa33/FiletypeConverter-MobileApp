package com.example.project.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

// Junction table — Many-to-Many between FileEntity and FileTagEntity
@Entity(
    tableName = "file_tag_cross_ref",
    primaryKeys = ["fileId", "tagId"]
)
data class FileTagCrossRef(
    val fileId: String,
    @ColumnInfo(index = true) val tagId: Int
)
