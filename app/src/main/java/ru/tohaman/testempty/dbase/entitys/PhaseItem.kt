package ru.tohaman.testempty.dbase.entitys

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity (tableName = "currentPhase", primaryKeys = ["id"])
data class PhaseItem (
    @ColumnInfo(name = "phase")
    val phase: String,
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "icon")
    val icon: Int = 0,
    @ColumnInfo(name = "description")
    val description: Int = 0,
    @ColumnInfo(name = "url")
    val urlOrType: String = "",
    @ColumnInfo(name = "comment")
    val comment: String = ""
)

/**
    By default, Room uses the field names as the column names in the database.
    If you want a column to have a different name, add the @ColumnInfo annotation to a field.
 */
