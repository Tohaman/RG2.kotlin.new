package ru.tohaman.rg2.dbase.daos

import androidx.room.*
import ru.tohaman.rg2.dbase.entitys.BasicMove

@Dao
interface MovesDao {
    @Query("SELECT * FROM basic_moves")
    suspend fun getAllItems() : List<BasicMove>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(basicMove: BasicMove)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(basicMoves: List<BasicMove>)

    @Query("SELECT * FROM basic_moves WHERE type = :type ORDER BY ID")
    suspend fun getTypeItems(type: String) : List<BasicMove>

    @Query("DELETE FROM basic_moves")
    suspend fun deleteAllItems()
}