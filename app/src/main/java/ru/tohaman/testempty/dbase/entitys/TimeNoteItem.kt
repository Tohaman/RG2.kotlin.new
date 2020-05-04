package ru.tohaman.testempty.dbase.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "TimesTable")
data class TimeNoteItem (
    @PrimaryKey
    val uuid : Int,
    var solvingTime : String = "0:05:57",
    val dateTime : String = "04/05/20 10:00",
    val scramble : String = "",
    var comment : String = ""
)

/**
    By default, Room uses the field names as the column names in the database.
    If you want a column to have a different name, add the @ColumnInfo annotation to a field.
 */
