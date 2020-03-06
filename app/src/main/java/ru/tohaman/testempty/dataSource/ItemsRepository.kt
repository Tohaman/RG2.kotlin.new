package ru.tohaman.testempty.dataSource

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.toLiveData
import org.koin.android.ext.android.inject

import ru.tohaman.testempty.dbase.MainDBItem
import ru.tohaman.testempty.dbase.MainDao
import ru.tohaman.testempty.dbase.MainDb
import ru.tohaman.testempty.dbase.PhaseItem

/**
 * The Repository ist a simple Java class that abstracts the data layer from the rest of the app
 * and mediates between different data sources, like a web service and a local cache. It hides the
 * different database operations (like SQLite queries) and provides a clean API to the ViewModel.

    Since Room doesn’t allow database queries on the main thread, we use AsyncTasks to execute them
    asynchronously in ioThread
 */

class ItemsRepository (private val dao : MainDao) : ItemDataSource {

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