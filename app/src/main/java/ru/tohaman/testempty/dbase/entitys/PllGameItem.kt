package ru.tohaman.testempty.dbase.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PLL_GAME")
data class PllGameItem (
  @PrimaryKey
  val id: Int,
  var InternationalName: String,
  var maximName: String,
  var userName: String,
  var currentName: String,
  var scramble: String,
  var imageRes: Int
)