package ru.tohaman.testempty.dbase.entitys

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "Azbuka", primaryKeys = ["azbukaName", "id"])
data class AzbukaItem (
    @PrimaryKey
    val id : Int,
    var azbukaName : String,
    var value : String,
    var color : Int
)