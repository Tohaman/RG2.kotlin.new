package ru.tohaman.rg2.dbase.daos

import androidx.room.*
import ru.tohaman.rg2.dbase.entitys.OldDbItem
import ru.tohaman.rg2.dbase.entitys.OldTimeItem


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
interface OldTimeDao {

    @Query("SELECT * FROM timeTable")
    suspend fun getAllOldTimeItems() : List<OldTimeItem>

}