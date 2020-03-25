package ru.tohaman.testempty.dbase.daos

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import ru.tohaman.testempty.dbase.entitys.MainDBItem
import ru.tohaman.testempty.dbase.entitys.PhaseItem


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
    }

    @Query("SELECT * FROM $table WHERE phase = 'BEGIN'")
    fun getAllLiveItems() : LiveData<List<MainDBItem>>

    @Query("SELECT *  FROM $table WHERE phase = :phase ORDER BY ID")
    suspend fun getPhaseFromMain(phase : String) : List<MainDBItem>

    @Query("SELECT *  FROM $table WHERE phase = :phase and url <> 'submenu' ORDER BY ID")
    suspend fun getDetailsItems(phase : String) : List<MainDBItem>

    @Query("SELECT *  FROM $table WHERE phase = :phase and id = :id")
    suspend fun getItem(phase : String, id: Int) : MainDBItem

    @Query("SELECT *  FROM $table WHERE isFavourite = 1")
    fun getFavourites() : List<MainDBItem>

    @Query("SELECT *  FROM $table WHERE isFavourite = 1")
    fun getLiveFavourites() : LiveData<List<MainDBItem>>

    @Query("SELECT *  FROM $table WHERE phase = :phase ORDER BY ID")
    fun getLivePhaseFromMain(phase : String) : LiveData<List<MainDBItem>>

    @Query("SELECT *  FROM $table WHERE phase = :phase ORDER BY ID")
    fun observePhase(phase : String) : LiveData<List<MainDBItem>>

    @Query("SELECT phase, id, title, icon, description, url, comment  FROM $table WHERE phase = :phase ORDER BY ID")
    suspend fun getPhase(phase : String): List<PhaseItem>

    @Query("SELECT distinct * FROM $table WHERE url = 'submenu' ORDER BY ID")
    suspend fun getSubMenuList(): List<MainDBItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mainDBItems: List<MainDBItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mainDBItem: MainDBItem?)

    @Update
    fun update(mainDBItem: MainDBItem?)

    @Update
    fun update(mainDBItem: List<MainDBItem>)

    @Delete
    fun delete(mainDBItem: MainDBItem?)

    @Query("DELETE FROM $table")
    suspend fun deleteAllItems()

}