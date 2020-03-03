package ru.tohaman.testempty.dbase

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*


/**
 * В объекте Dao мы будем описывать методы для работы с базой данных и соответствующие им селекты/инсерты/апдйеты и т.д.
 * Это интерфейс для работы с базой Room, если запрос возвращает не LiveData<>, то он должен выполняться
 * не в основном потоке.
 * Для вставки/обновления/удаления используются методы insert/update/delete с соответствующими
 * аннотациями. Тут никакие запросы указывать не нужно. Названия методов могут быть любыми.
 * Главное - аннотации.
 * Имя таблицы = имя класса помеченного как @Entity или заданное в нем через tableName = "mainTable"
*/

@Dao
interface MainDao {

    companion object {
        const val table: String = "mainTable"
        const val curTable: String = "currentPhase"
    }

    @Query("SELECT * FROM $table WHERE phase = 'BEGIN'")
    fun getAllLiveItems() : LiveData<List<MainDBItem>>

    @Query("SELECT *  FROM $table WHERE phase = :phase ORDER BY ID")
    suspend fun getPhaseFromMain(phase : String) : List<MainDBItem>

    @Query("SELECT *  FROM $table WHERE phase = :phase ORDER BY ID")
    fun observePhase(phase : String) : LiveData<List<MainDBItem>>

    @Query("SELECT phase, id, title, icon, description, url, comment  FROM $table WHERE phase = :phase ORDER BY ID")
    suspend fun getPhase(phase : String): List<PhaseItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mainDBItems: List<MainDBItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mainDBItem: MainDBItem?)

    @Update
    fun update(mainDBItem: MainDBItem?)

    @Delete
    fun delete(mainDBItem: MainDBItem?)

    @Query("DELETE FROM $table")
    suspend fun deleteAllItems()

    //Запросы для CurrentTable

    @Query("SELECT * FROM $curTable")
    fun getCurrentPhase(): DataSource.Factory<Int, PhaseItem>

    @Query ("DELETE FROM $curTable")
    suspend fun deleteCurrentItems()

    @Insert
    suspend fun insertCurrentItems(phaseItems: List<PhaseItem>)

}