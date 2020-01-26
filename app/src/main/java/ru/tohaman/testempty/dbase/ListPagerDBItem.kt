package ru.tohaman.testempty.dbase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (primaryKeys = ["phase", "id"])
data class ListPagerDBItem (
    val phase: String,
    val id: Int,
    val title: String = "",
    val icon: Int = 0,
    val description: Int = 0,
    val url: String = "",
    val comment: String = "",
    val subID: String = "",
    val subTitle: String = "",
    val subLongTitle: String = ""
)

/**
    By default, Room uses the field names as the column names in the database.
    If you want a column to have a different name, add the @ColumnInfo annotation to a field.
 */
