package ru.tohaman.testempty.dbase

import android.app.Application
import androidx.paging.Config
import androidx.paging.toLiveData
import ru.tohaman.testempty.utils.ioThread
import java.util.concurrent.Phaser

/**
 * The Repository ist a simple Java class that abstracts the data layer from the rest of the app
 * and mediates between different data sources, like a web service and a local cache. It hides the
 * different database operations (like SQLite queries) and provides a clean API to the ViewModel.

    Since Room doesn’t allow database queries on the main thread, we use AsyncTasks to execute them
    asynchronously in ioThread
 */

class ItemsRepository (app: Application){

    private val dao = MainDb.get(app).listPagerDao()
    private var allItems  = dao.getAllItems()

    //fun getPhaseItems (phase: String) = dao.getPhase(phase).toLiveData(Config(30))

    fun getAllItems() = dao.getAllItems()

    fun getCurrentPhase() = dao.getCurrentPhase().toLiveData(Config(30))

    fun insert(tableItem: MainDBItem) {
        ioThread { dao.insert(tableItem) }
    }

    fun remove(tableItem: MainDBItem) = ioThread {
        dao.delete(tableItem)
    }

    fun deleteAllItems() = ioThread {
        dao.deleteAllItems()
    }

    fun clearCurrentTable() = ioThread {
        dao.deleteCurrentItems()
    }

    fun changePhase(phase: String) {
        ioThread {
            dao.deleteCurrentItems()
            val curPhaseList = dao.getPhase(phase)
            dao.insertCurrentItems(curPhaseList)
        }
    }

}