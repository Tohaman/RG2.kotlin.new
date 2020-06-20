package ru.tohaman.rg2.dbase.entitys

import androidx.room.Entity

/**
 * ДатаКласс используемый для создания записей в таблице.
 * В качестве имени таблицы будет использовано имя класса. Но можно его поменять при помощи
 * аннотации tableName = "mainTable"
 * Поля таблицы будут созданы в соответствии с полями класса. Для смены имени поля
 * аннотация @ColumnInfo(name = "anotherPhase")
 * Аннотацией PrimaryKey мы помечаем поле/поля, которые будут ключом в таблице.
 * Комбинированный ключ - primaryKeys = ["phase", "id"])
 */

@Entity (tableName = "main", primaryKeys = ["phase", "id"])
data class MainDBItem (
    //@ColumnInfo(name = "phase")
    val phase: String,
    //@PrimaryKey (autoGenerate = true)
    val id: Int,
    var title: String = "",
    var icon: Int = 0,
    var description: Int = 0,
    var url: String = "",
    var comment: String = "",
    var isFavourite: Boolean = false,
    var favComment: String = "",
    var subId: Int = 0
)