package ru.tohaman.rg2.dbase.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PLL_GAME")
data class PllGameItem (
  @PrimaryKey
  val id: Int,
  val internationalName: String,
  val maximName: String,
  var userName: String,
  var currentName: String,
  val scramble: String,
  val imageRes: Int,
  var isChecked: Boolean
)