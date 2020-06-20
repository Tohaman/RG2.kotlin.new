package ru.tohaman.rg2.dbase.daos

import androidx.room.*
import ru.tohaman.rg2.dbase.entitys.AzbukaDBItem

@Dao
interface AzbukaDao {
    @Query("SELECT * FROM azbuka")
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

    @Query("DELETE FROM azbuka")
    suspend fun deleteAllItems()
}