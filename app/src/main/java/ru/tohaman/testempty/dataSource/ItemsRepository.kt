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
import timber.log.Timber

/**
 * The Repository ist a simple Java class that abstracts the data layer from the rest of the app
 * and mediates between different data sources, like a web service and a local cache. It hides the
 * different database operations (like SQLite queries) and provides a clean API to the ViewModel.

    Since Room doesn’t allow database queries on the main thread, we use AsyncTasks to execute them
    asynchronously in ioThread
 */

class ItemsRepository : ItemDataSource {

    private val dao = mainDatabase.listPagerDao
    //private var allItems  = dao.getAllItems()


    fun observePhase(phase: String): LiveData<List<MainDBItem>> {
        return dao.observePhase(phase)
    }

    suspend fun getPhaseFromMain(phase: String): List<MainDBItem> {
        Timber.d ("getPhaseFromMain with $phase")
        return dao.getPhaseFromMain(phase)
    }

    fun getAllLiveDataItems() = dao.getAllLiveItems()

    fun getCurrentPhase() = dao.getCurrentPhase().toLiveData(Config(30))

    suspend fun insert(tableItem: MainDBItem) {
         dao.insert(tableItem)
    }

}