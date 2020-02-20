package ru.tohaman.testempty.dataSource

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import ru.tohaman.testempty.dbase.MainDBItem
import ru.tohaman.testempty.dbase.PhaseItem
import ru.tohaman.testempty.dbase.mainDatabase
import ru.tohaman.testempty.utils.ioThread

/**
 * The Repository ist a simple Java class that abstracts the data layer from the rest of the app
 * and mediates between different data sources, like a web service and a local cache. It hides the
 * different database operations (like SQLite queries) and provides a clean API to the ViewModel.

    Since Room doesn’t allow database queries on the main thread, we use AsyncTasks to execute them
    asynchronously in ioThread
 */

class ItemsRepository : ItemDataSource {

    private val dao = mainDatabase.listPagerDao
    private var allItems  = dao.getAllItems()

    override fun observePhase(phase: String): LiveData<List<MainDBItem>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPhaseFromMain(phase: String): LiveData<List<MainDBItem>> {
        return dao.getPhaseFromMain(phase)
    }

    //fun getPhaseItems (phase: String) = dao.getPhase(phase).toLiveData(Config(30))

    override fun getAllItems() = dao.getAllItems()

    override fun getCurrentPhase() = dao.getCurrentPhase().toLiveData(Config(30))

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