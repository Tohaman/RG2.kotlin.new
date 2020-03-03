package ru.tohaman.testempty.dataSource

import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.toLiveData
import ru.tohaman.testempty.dbase.MainDBItem
import ru.tohaman.testempty.dbase.PhaseItem
import ru.tohaman.testempty.dbase.mainDatabase
import timber.log.Timber

/**
 * The Repository ist a simple Java class that abstracts the data layer from the rest of the app
 * and mediates between different data sources, like a web service and a local cache. It hides the
 * different database operations (like SQLite queries) and provides a clean API to the ViewModel.

    Since Room doesn’t allow database queries on the main thread, we use AsyncTasks to execute them
    asynchronously in ioThread
 */

class ItemsRepository : ItemDataSource {

    private val dao = mainDatabase.dao

    fun observePhase(phase: String): LiveData<List<MainDBItem>> {
        return dao.observePhase(phase)
    }

    suspend fun getPhase(phase: String) : List<PhaseItem> = dao.getPhase(phase)

    suspend fun getPhaseFromMain(phase: String): List<MainDBItem> = dao.getPhaseFromMain(phase)

    fun getAllLiveDataItems() = dao.getAllLiveItems()

    fun getCurrentPhase() = dao.getCurrentPhase().toLiveData(Config(30))

    suspend fun clearMainTable() = dao.deleteAllItems()

    suspend fun clearCurrentTable() = dao.deleteCurrentItems()

    suspend fun insertItems2CurrentTable(curPhaseList : List<PhaseItem>) = dao.insertCurrentItems(curPhaseList)

    suspend fun insert(tableItem: MainDBItem) = dao.insert(tableItem)

}