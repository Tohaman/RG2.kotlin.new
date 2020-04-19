package ru.tohaman.testempty.dbase.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AzbukaItem (
    @PrimaryKey
    val id : Int,
    val azbukaName : String,
    val initPhase : String,
    var curPhase : String
)