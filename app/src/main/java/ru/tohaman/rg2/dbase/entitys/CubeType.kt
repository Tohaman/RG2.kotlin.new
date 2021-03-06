package ru.tohaman.rg2.dbase.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cube_types")
data class CubeType (
    @PrimaryKey
    val id : Int,
    val name : String,
    val initPhase : String,
    var curPhase : String
)