package ru.tohaman.testempty.dbase.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.tohaman.testempty.dbase.entitys.CubeType

@Dao
interface CubeTypesDao {
    @Query("SELECT * FROM CubeType")
    suspend fun getAllItems() : List<CubeType>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cubeType: CubeType)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cubeType: List<CubeType>)

    @Query("DELETE FROM CubeType")
    suspend fun deleteAllItems()
}