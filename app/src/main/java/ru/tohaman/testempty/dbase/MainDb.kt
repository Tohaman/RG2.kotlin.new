package ru.tohaman.testempty.dbase

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.tohaman.testempty.dbase.daos.AzbukaDao
import ru.tohaman.testempty.dbase.daos.CubeTypesDao
import ru.tohaman.testempty.dbase.daos.MainDao
import ru.tohaman.testempty.dbase.daos.MovesDao
import ru.tohaman.testempty.dbase.entitys.*

/**
 * Аннотацией Database помечаем основной класс по работе с базой данных. Этот класс должен быть
 * абстрактным и наследовать RoomDatabase. В параметрах аннотации Database указываем, какие Entity будут использоваться, и версию базы.
 * Для каждого Entity класса из списка entities будет создана таблица.
 * В Database классе необходимо описать абстрактные методы для получения Dao объектов, которые нам понадобятся.
 * В аннотации entities задаем используемые в базе entities, по сути - таблицы.
 * Room сравнивает поля в entities, и если они отличаются от текущих в базе, то нужна миграция, а соответственно повышение версии базы
 */

@Database(entities = [MainDBItem::class, CubeType::class, BasicMove::class, AzbukaDBItem::class], version = 8)
abstract class MainDb : RoomDatabase() {
    abstract val mainDao : MainDao
    abstract val cubeTypesDao : CubeTypesDao
    abstract val movesDao : MovesDao
    abstract val azbukaDao: AzbukaDao
}
