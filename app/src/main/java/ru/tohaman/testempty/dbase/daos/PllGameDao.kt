package ru.tohaman.testempty.dbase.daos

import androidx.room.*
import ru.tohaman.testempty.dbase.entitys.BasicMove
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.dbase.entitys.PllGameItem

@Dao
interface PllGameDao {
    @Query("SELECT * FROM PLL_GAME")
    suspend fun getAllItems() : List<PllGameItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pllGameItem: PllGameItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pllGameItem: List<PllGameItem>)

    @Query("SELECT * FROM PLL_GAME WHERE id = :id")
    suspend fun getPllItem(id: Int) : PllGameItem

    @Query("DELETE FROM PLL_GAME")
    suspend fun deleteAllItems()
}