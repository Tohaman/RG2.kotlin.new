package ru.tohaman.testempty.dbase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dataSource.applicationLiveData
import ru.tohaman.testempty.dataSource.getApplication
import ru.tohaman.testempty.utils.DebugTag.TAG
import ru.tohaman.testempty.utils.ioThread
import timber.log.Timber

/**
Аннотацией Database помечаем основной класс по работе с базой данных. Этот класс должен быть
абстрактным и наследовать RoomDatabase. В параметрах аннотации Database указываем, какие Entity будут использоваться, и версию базы.
Для каждого Entity класса из списка entities будет создана таблица.
В Database классе необходимо описать абстрактные методы для получения Dao объектов, которые нам понадобятся.
 */

private const val DATABASE_NAME = "base.db"

val mainDatabase: MainDb by lazy { buildDataBase(applicationLiveData.getApplication())}

private fun buildDataBase(context: Context) = Room.databaseBuilder(
    context, MainDb::class.java,
    DATABASE_NAME)
    .addCallback(object : RoomDatabase.Callback() {
        //TODO заменить на onCreate, чтобы пересоздавалась, только если еще нет, а не при каждом запуске
        override fun onOpen(db: SupportSQLiteDatabase) {
            Timber.tag(TAG).d ("ReCreate database!!! Fill with new data")
            MainDb.fillLPinDB(context)
        }
    }).build()


@Database(entities = [MainDBItem::class, PhaseItem::class], version = 1)
abstract class MainDb : RoomDatabase() {

    abstract val listPagerDao : ListPagerDao

    //TODO настроить компаньон объект, чтобы при создании базы, она заполнялась какими-то значениями, можно брать из произвольного файла
    //Room.databaseBuilder(appContext, AppDatabase.class, "Sample.db")
    //.createFromFile(File("mypath"))
    //.build()

    companion object {
//        private var instance: MainDb? = null
//        @Synchronized
//        fun gets(context: Context): MainDb {
//            if (instance == null) {
//                instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    MainDb::class.java, "base.db"
//                ).addCallback(object : RoomDatabase.Callback() {
//
//                        override fun onOpen(db: SupportSQLiteDatabase) {
//                            fillLPinDB(context.applicationContext)
//                        }
//                    }).build()
//            }
//            return instance!!
//        }

        /**
         * fill database with list of SOME_DATA
         */

        fun fillLPinDB (context: Context) {
            // inserts in Room are executed on the current thread, so we insert in the background
            ioThread {
                Timber.d ("FillDb with data")
                mainDatabase.listPagerDao.deleteAllItems()
                insertPhasesToMainTable(context)
                insertCurrentPhases()
            }
        }

        private fun insertCurrentPhases() {
            mainDatabase.listPagerDao.deleteCurrentItems()
            val curPhaseList = mainDatabase.listPagerDao.getPhase("BEGIN")
            mainDatabase.listPagerDao.insertCurrentItems(curPhaseList)
        }

        private fun insertPhasesToMainTable(context: Context) {
            //subMenu
            phaseInit("MAIN3X3", R.array.main3x3_title, R.array.main3x3_icon, R.array.main3x3_descr, R.array.main3x3_url, context)
            //Phases
            phaseInit("BEGIN", R.array.begin_title, R.array.begin_icon, R.array.begin_descr, R.array.begin_url, context)
            phaseInit("ROZOV", R.array.begin_rozov_title, R.array.begin_rozov_icon, R.array.begin_rozov_descr, R.array.begin_rozov_url, context)
        }


        // Инициализация фазы, с заданными массивами Заголовков, Иконок, Описаний, ютуб-ссылок
        private fun phaseInit(phase: String, titleArray: Int, iconArray: Int, descrArray: Int, urlArray: Int, context: Context, comment : Int = 0) {
            val emptyComment = Array (100) {""}
            val titles =  context.resources.getStringArray(titleArray)
            val icon = context.resources.obtainTypedArray (iconArray)
            val description = context.resources.obtainTypedArray (descrArray)
            val url = context.resources.getStringArray(urlArray)
            val cmnt = if (comment != 0) { context.resources.getStringArray(comment) } else { emptyComment}
            for (i in titles.indices) {
                val iconID = icon.getResourceId(i, 0)
                val testIcon = R.drawable.axis_6
                val descriptionID = description.getResourceId(i, 0)
                val listPager = MainDBItem(phase, i, titles[i], iconID, descriptionID, url[i], cmnt[i])
                mainDatabase.listPagerDao.insert(listPager)
            }
            icon.recycle()
            description.recycle()

        }

    }

}
