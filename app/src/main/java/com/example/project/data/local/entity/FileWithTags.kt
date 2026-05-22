package com.example.project.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// Many-to-Many: one File can have many Tags (and vice versa)
data class FileWithTags(
    @Embedded val file: FileEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = FileTagCrossRef::class,
            parentColumn = "fileId",
            entityColumn = "tagId"
        )
    )
    val tags: List<FileTagEntity>
)
