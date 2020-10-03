package ru.tohaman.rg2.dbase.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.tohaman.rg2.dbase.entitys.PhasePositionItem

@Dao
interface PhasePositionDao {
    @Query("SELECT * FROM phasePositions")
    suspend fun getAllPositions() : List<PhasePositionItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplacePhasePosition(phasePositionItem: PhasePositionItem)

    @Query("DELETE FROM phasePositions")
    suspend fun clearPhasePositions()
}