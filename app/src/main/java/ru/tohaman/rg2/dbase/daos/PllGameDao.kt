package ru.tohaman.rg2.dbase.daos

import androidx.room.*
import ru.tohaman.rg2.dbase.entitys.PllGameItem

@Dao
interface PllGameDao {
    @Query("SELECT * FROM PLL_GAME")
    suspend fun getAllItems() : List<PllGameItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pllGameItem: PllGameItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pllGameItem: List<PllGameItem>)

    @Update
    suspend fun update(pllGameItem: List<PllGameItem>)

    @Update
    suspend fun update(pllGameItem: PllGameItem)

    @Query("SELECT * FROM PLL_GAME WHERE id = :id")
    suspend fun getPllItem(id: Int) : PllGameItem?

    @Query("DELETE FROM PLL_GAME")
    suspend fun deleteAllItems()
}