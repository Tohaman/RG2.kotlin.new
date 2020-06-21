package ru.tohaman.rg2.dbase.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Класс для доступа к базе времени таймера SQLite старых версий (до миграции на AndroidX)
 */

@Entity (tableName = "timeTable")
data class OldTimeItem (
    @PrimaryKey
    val uuid: Int,
    val currentTime: String = "",
    val dateOfNote: String = "",
    val timeComment: String = "",
    var scramble: String = ""
)