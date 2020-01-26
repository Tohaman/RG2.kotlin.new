package ru.tohaman.testempty.dbase

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.room.*


/**
 * В объекте Dao мы будем описывать методы для работы с базой данных и соответствующие им селекты/инсерты/апдйеты и т.д.
 */



@Dao
interface ListPagerDao {

    companion object {
        const val table: String = "ListPagerDBItem"
    }

    @Query("SELECT * FROM $table")
    fun getAllItems(): LiveData<List<ListPagerDBItem?>?>

    @Query("SELECT * FROM $table WHERE phase = :phase ORDER BY ID")
    fun getPhase(phase : String): DataSource.Factory<Int, ListPagerDBItem>

    @Insert
    fun insert(listPagerDBItems: List<ListPagerDBItem>)

    @Insert
    fun insert(listPagerDBItem: ListPagerDBItem?)

    @Update
    fun update(listPagerDBItem: ListPagerDBItem?)

    @Delete
    fun delete(listPagerDBItem: ListPagerDBItem?)

    @Query("DELETE FROM $table")
    fun deleteAllItems()

}