package ru.tohaman.rg2.dbase.daos

import androidx.room.*
import ru.tohaman.rg2.dbase.entitys.CubeType

@Dao
interface CubeTypesDao {
    @Query("SELECT * FROM CubeType")
    suspend fun getAllItems() : List<CubeType>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cubeType: CubeType)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cubeType: List<CubeType>)

    @Update
    suspend fun update(cubeType: CubeType)

    @Update
    suspend fun update(cubeType: List<CubeType>)

    @Query("DELETE FROM CubeType")
    suspend fun deleteAllItems()
}