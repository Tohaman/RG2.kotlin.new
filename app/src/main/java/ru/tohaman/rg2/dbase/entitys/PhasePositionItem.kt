package ru.tohaman.rg2.dbase.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Класс для одной записи для хранения состояния RecycleView (номера последней позиции)
 */
@Entity(tableName = "phasePositions")
data class PhasePositionItem(
    @PrimaryKey
    val phase: String,
    val position: Int
)