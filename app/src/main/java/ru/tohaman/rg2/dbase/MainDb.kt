package ru.tohaman.rg2.dbase


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.tohaman.rg2.dbase.daos.*
import ru.tohaman.rg2.dbase.entitys.*

/**
 * Аннотацией Database помечаем основной класс по работе с базой данных. Этот класс должен быть
 * абстрактным и наследовать RoomDatabase. В параметрах аннотации Database указываем, какие Entity будут использоваться, и версию базы.
 * Для каждого Entity класса из списка entities будет создана таблица.
 * В Database классе необходимо описать абстрактные методы для получения Dao объектов, которые нам понадобятся.
 * В аннотации entities задаем используемые в базе entities, по сути - таблицы.
 * Room сравнивает поля в entities, и если они отличаются от текущих в базе, то нужна миграция, а соответственно повышение версии базы
 */

@Database(
    entities = [MainDBItem::class,
        CubeType::class, BasicMove::class,
        AzbukaDBItem::class, TimeNoteItem::class,
        PllGameItem::class],
    version = 1
)
@TypeConverters(LocalDateConverters::class)
abstract class MainDb : RoomDatabase() {
    abstract val mainDao : MainDao
    abstract val cubeTypesDao : CubeTypesDao
    abstract val movesDao : MovesDao
    abstract val azbukaDao: AzbukaDao
    abstract val timeNoteDao: TimeNoteDao
    abstract val pllGameDao: PllGameDao
}
