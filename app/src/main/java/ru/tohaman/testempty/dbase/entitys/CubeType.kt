package ru.tohaman.testempty.dbase.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CubeType (
    @PrimaryKey
    val id : Int,
    val name : String,
    val initPhase : String,
    var curPhase : String
)