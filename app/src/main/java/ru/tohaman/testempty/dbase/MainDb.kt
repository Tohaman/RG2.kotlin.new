package ru.tohaman.testempty.dbase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.*
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dataSource.applicationLiveData
import ru.tohaman.testempty.dataSource.getApplication
import timber.log.Timber

/**
 * Аннотацией Database помечаем основной класс по работе с базой данных. Этот класс должен быть
 * абстрактным и наследовать RoomDatabase. В параметрах аннотации Database указываем, какие Entity будут использоваться, и версию базы.
 * Для каждого Entity класса из списка entities будет создана таблица.
 * В Database классе необходимо описать абстрактные методы для получения Dao объектов, которые нам понадобятся.
 * Поскольку создание такого класса "дорогое удовольствие", то используем или синглтон (синглет)
 * через создание в "companion object instance" или глабоальную переменную [mainDatabase]
 * В аннотации entities задаем используемые в базе entities, по сути - таблицы.
 * Room сравнивает поля в entities, и если они отличаются от текущих в базе, то нужна миграция, а соответственно повышение версии базы
 */

private const val DATABASE_NAME = "base.db"

val mainDatabase: MainDb by lazy { buildDataBase(applicationLiveData.getApplication())}

private fun buildDataBase(context: Context) = MainDb.get(context)


@Database(entities = [MainDBItem::class, PhaseItem::class], version = 1)
abstract class MainDb : RoomDatabase() {

    abstract val dao : MainDao

    //TODO настроить компаньон объект, чтобы при создании базы, она заполнялась какими-то значениями, можно брать из произвольного файла
    //Room.databaseBuilder(appContext, AppDatabase.class, "Sample.db")
    //.createFromFile(File("mypath"))
    //.build()

    companion object {
        private var instance: MainDb? = null
        @Synchronized
        fun get(context: Context): MainDb {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDb::class.java, "base.db"
                )
//                    .addCallback(object : RoomDatabase.Callback() {
//                        override fun onOpen(db: SupportSQLiteDatabase) { fillDB(context.applicationContext) }
//                    })
                    .build()
            }
            return instance!!
        }
    }

}
