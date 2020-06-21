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
    entities = [OldDbItem::class, OldTimeItem::class],
    version = 4
)
@TypeConverters(LocalDateConverters::class)
abstract class OldDb : RoomDatabase() {
    abstract val oldBaseDao: OldBaseDao
    abstract val oldTimeDao: OldTimeDao
}
