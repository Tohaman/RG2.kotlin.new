package ru.tohaman.rg2.dbase.entitys

import androidx.room.Entity

@Entity (tableName = "azbuka", primaryKeys = ["azbukaName", "id"])
data class AzbukaDBItem (
    var azbukaName : String,
    var id : Int,
    var value : String,
    var color : Int
)