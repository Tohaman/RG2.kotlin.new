package ru.tohaman.rg2.dbase.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "TimesTable")
data class TimeNoteItem (
    @PrimaryKey (autoGenerate = true)
    val uuid : Int,
    var solvingTime : String = "0:05:57",
    val dateTime : Calendar? = null,
    val scramble : String = "",
    var comment : String = ""
)

/**
    By default, Room uses the field names as the column names in the database.
    If you want a column to have a different name, add the @ColumnInfo annotation to a field.
 */
