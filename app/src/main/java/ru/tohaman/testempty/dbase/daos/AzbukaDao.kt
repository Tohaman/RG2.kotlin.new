package ru.tohaman.testempty.dbase.daos

import androidx.room.*
import ru.tohaman.testempty.dbase.entitys.AzbukaDBItem
import ru.tohaman.testempty.dbase.entitys.BasicMove
import ru.tohaman.testempty.dbase.entitys.CubeType

@Dao
interface AzbukaDao {
    @Query("SELECT * FROM Azbuka")
    suspend fun getAllItems() : List<AzbukaDBItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(azbukaItem: AzbukaDBItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(azbukaItem: List<AzbukaDBItem>)

    @Query("SELECT * FROM Azbuka WHERE azbukaName = :azbukaName ORDER BY ID")
    suspend fun getAzbukaItems(azbukaName: String) : List<AzbukaDBItem>

    @Update
    suspend fun update(azbukaItem: AzbukaDBItem)

    @Update
    suspend fun update(azbukaItem: List<AzbukaDBItem>)

    @Query("DELETE FROM Azbuka")
    suspend fun deleteAllItems()
}