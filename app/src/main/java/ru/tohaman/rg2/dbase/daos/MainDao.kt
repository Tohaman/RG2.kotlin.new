package ru.tohaman.rg2.dbase.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.tohaman.rg2.dbase.entitys.MainDBItem


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
        const val table: String = "main"
    }

    @Query("SELECT * FROM $table")
    suspend fun getAllItems() : List<MainDBItem>

    @Query("SELECT *  FROM $table WHERE phase = :phase ORDER BY ID")
    suspend fun getPhaseFromMain(phase : String) : List<MainDBItem>

    @Query("SELECT *  FROM $table WHERE phase = :phase and url <> 'submenu' ORDER BY ID")
    suspend fun getDetailsItems(phase : String) : List<MainDBItem>

    @Query("SELECT *  FROM $table WHERE phase = :phase and id = :id")
    suspend fun getItem(phase : String, id: Int) : MainDBItem?

    @Query("SELECT *  FROM $table WHERE isFavourite = 1 ORDER BY SubID")
    suspend fun getFavourites() : List<MainDBItem>

    @Query("SELECT distinct * FROM $table WHERE url = 'submenu' ORDER BY ID")
    suspend fun getSubMenuList(): List<MainDBItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mainDBItems: List<MainDBItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mainDBItem: MainDBItem?)

    @Update
    suspend fun update(mainDBItem: MainDBItem?): Int

    @Update
    suspend fun update(mainDBItem: List<MainDBItem>): Int

    @Delete
    fun delete(mainDBItem: MainDBItem?)

    @Query("DELETE FROM $table")
    suspend fun deleteAllItems()

}