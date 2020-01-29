package ru.tohaman.testempty.dbase

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "currenPhase", primaryKeys = ["phase", "id"])
data class CurrentPhase (
    //Делаем структуру таблицы, как у таблицы MainTable, т.е. из ListPagerDBItem
    @Embedded
    var curPhase : ListPagerDBItem
)

/**
    By default, Room uses the field names as the column names in the database.
    If you want a column to have a different name, add the @ColumnInfo annotation to a field.
 */
