package ru.tohaman.testempty.dbase.entitys

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ДатаКласс используемый для создания записей в таблице.
 * В качестве имени таблицы будет использовано имя класса. Но можно его поменять при помощи
 * аннотации tableName = "mainTable"
 * Поля таблицы будут созданы в соответствии с полями класса. Для смены имени поля
 * аннотация @ColumnInfo(name = "anotherPhase")
 * Аннотацией PrimaryKey мы помечаем поле/поля, которые будут ключом в таблице.
 * Комбинированный ключ - primaryKeys = ["phase", "id"])
 */

@Entity (tableName = "mainTable", primaryKeys = ["phase", "id"])
data class MainDBItem (
    //@ColumnInfo(name = "phase")
    val phase: String,
    //@PrimaryKey (autoGenerate = true)
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