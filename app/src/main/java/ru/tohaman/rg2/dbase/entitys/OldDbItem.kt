package ru.tohaman.rg2.dbase.entitys

import androidx.room.Entity

/**
 * Класс для доступа к основной базе SQLite старых версий (до миграции на AndroidX)
 */

@Entity (tableName = "baseTable", primaryKeys = ["phase", "id"])
data class OldDbItem (
    val phase: String,
    val id: Int = 0,
    val comment: String = "",
    var subId: Int = 0
)