package ru.tohaman.testempty.dataSource

import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.toLiveData
import kotlinx.coroutines.delay
import ru.tohaman.testempty.dbase.daos.AzbukaDao
import ru.tohaman.testempty.dbase.daos.CubeTypesDao

import ru.tohaman.testempty.dbase.daos.MainDao
import ru.tohaman.testempty.dbase.daos.MovesDao
import ru.tohaman.testempty.dbase.entitys.*

/**
 * The Repository ist a simple Java class that abstracts the data layer from the rest of the app
 * and mediates between different data sources, like a web service and a local cache. It hides the
 * different database operations (like SQLite queries) and provides a clean API to the ViewModel.

    Since Room doesn’t allow database queries on the main thread, then we use suspend fun
 */

class ItemsRepository (private val mainDao : MainDao,
                       private val typeDao: CubeTypesDao,
                       private val movesDao: MovesDao,
                       private val azbukaDao: AzbukaDao) : ItemDataSource {

    // Работа с основной таблицей

    suspend fun getPhase(phase: String) : List<PhaseItem> = mainDao.getPhase(phase)

    suspend fun getSubMenuList() : List<MainDBItem> = mainDao.getSubMenuList()

    suspend fun getPhaseFromMain(phase: String): List<MainDBItem> = mainDao.getPhaseFromMain(phase)

    suspend fun getDetailsItems(phase: String): List<MainDBItem> = mainDao.getDetailsItems(phase)

    suspend fun getItem(phase: String, id: Int): MainDBItem = mainDao.getItem(phase, id)

    fun getLivePhaseFromMain(phase: String): LiveData<List<MainDBItem>> = mainDao.getLivePhaseFromMain(phase)

    suspend fun getFavourites(): List<MainDBItem> = mainDao.getFavourites()

    fun getLiveFavourites(): LiveData<List<MainDBItem>> = mainDao.getLiveFavourites()

    fun getAllLiveDataItems() = mainDao.getAllLiveItems()


    suspend fun clearMainTable() = mainDao.deleteAllItems()

    suspend fun insert2Main(item: MainDBItem) = mainDao.insert(item)

    suspend fun insert2Main(items: List<MainDBItem>) = mainDao.insert(items)

    suspend fun updateMainItem(item: MainDBItem?) = mainDao.update(item)

    suspend fun updateMainItem(items: List<MainDBItem>) = mainDao.update(items)

    // Работа с таблицей Types

    suspend fun getCubeTypes() = typeDao.getAllItems()

    suspend fun clearTypesTable() = typeDao.deleteAllItems()

    suspend fun insert2Type(item: CubeType) = typeDao.insert(item)

    suspend fun insert2Type(items: List<CubeType>) = typeDao.insert(items)

    suspend fun update(item: CubeType) = typeDao.update(item)

    suspend fun update(items: List<CubeType>) = typeDao.update(items)

    // Работа с таблицей Moves
    suspend fun insert2Moves(items: List<BasicMove>) = movesDao.insert(items)

    suspend fun getTypeItems(type: String): List<BasicMove> {
        //delay(2000) //имитируем задержку чтения в 2 сек
        return movesDao.getTypeItems(type)
    }
    suspend fun clearMovesTable() = movesDao.deleteAllItems()


    // Работа с таблицей Azbuka

    suspend fun getAzbukaItems(azbukaName: String) = azbukaDao.getAzbukaItems(azbukaName)

    suspend fun insertAzbuka(azbukaItem: List<AzbukaDBItem>) = azbukaDao.insert(azbukaItem)

    suspend fun updateAzbuka(azbukaItem: List<AzbukaDBItem>) = azbukaDao.update(azbukaItem)
}