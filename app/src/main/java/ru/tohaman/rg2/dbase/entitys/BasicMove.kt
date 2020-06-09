package ru.tohaman.rg2.dbase.entitys

import androidx.room.Entity

@Entity(tableName = "BasicMove", primaryKeys = ["type", "id"])
data class BasicMove (
    val id: Int,
    val type: String,       //тип головоломки
    val move: String ="",   //ход R, L, U'...
    val icon: Int,
    val toast: String =""  //Подсказка
)