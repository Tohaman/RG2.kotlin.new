package ru.tohaman.testempty.dbase.daos

import androidx.room.*
import ru.tohaman.testempty.dbase.entitys.BasicMove
import ru.tohaman.testempty.dbase.entitys.CubeType

@Dao
interface MovesDao {
    @Query("SELECT * FROM BasicMove")
    suspend fun getAllItems() : List<BasicMove>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(basicMove: BasicMove)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(basicMoves: List<BasicMove>)

    @Query("SELECT * FROM BasicMove WHERE type = :type ORDER BY ID")
    suspend fun getTypeItems(type: String) : List<BasicMove>

    @Query("DELETE FROM BasicMove")
    suspend fun deleteAllItems()
}