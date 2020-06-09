package ru.tohaman.rg2.dataSource

import androidx.lifecycle.LiveData
import ru.tohaman.rg2.dbase.daos.*

import ru.tohaman.rg2.dbase.entitys.*

/**
 * The Repository ist a simple Java class that abstracts the data layer from the rest of the app
 * and mediates between different data sources, like a web service and a local cache. It hides the
 * different database operations (like SQLite queries) and provides a clean API to the ViewModel.

    Since Room doesn’t allow database queries on the main thread, then we use suspend fun
 */

class ItemsRepository (private val mainDao : MainDao,
                       private val typeDao: CubeTypesDao,
                       private val movesDao: MovesDao,
                       private val azbukaDao: AzbukaDao,
                       private val timeNoteDao: TimeNoteDao,
                       private val pllGameDao: PllGameDao) : ItemDataSource {

    // Работа с основной таблицей

    suspend fun getPhase(phase: String) : List<PhaseItem> = mainDao.getPhase(phase)

    suspend fun getSubMenuList() : List<MainDBItem> = mainDao.getSubMenuList()

    suspend fun getPhaseFromMain(phase: String): List<MainDBItem> = mainDao.getPhaseFromMain(phase)

    suspend fun getDetailsItems(phase: String): List<MainDBItem> = mainDao.getDetailsItems(phase)

    suspend fun getItem(phase: String, id: Int): MainDBItem = mainDao.getItem(phase, id)

    fun getLivePhaseFromMain(phase: String): LiveData<List<MainDBItem>> = mainDao.getLivePhaseFromMain(phase)

    suspend fun getFavourites(): List<MainDBItem> = mainDao.getFavourites()

    suspend fun clearMainTable() = mainDao.deleteAllItems()

    suspend fun insert2Main(item: MainDBItem) = mainDao.insert(item)

    suspend fun insert2Main(items: List<MainDBItem>) = mainDao.insert(items)

    fun updateMainItem(item: MainDBItem?) = mainDao.update(item)

    fun updateMainItem(items: List<MainDBItem>) = mainDao.update(items)

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


    // Работа с таблицей TimesTable

    suspend fun getAllTimeNotes() = timeNoteDao.getAllTimeNotes()

    suspend fun insertTimeNote(timeNoteItem: TimeNoteItem) = timeNoteDao.insertTimeNote(timeNoteItem)

    suspend fun updateTimeNote(timeNoteItem: TimeNoteItem) = timeNoteDao.updateTimeNote(timeNoteItem)

    suspend fun deleteTimeNote(timeNoteItem: TimeNoteItem) = timeNoteDao.deleteTimeNote(timeNoteItem)

    suspend fun deleteAllTimeNotes() = timeNoteDao.deleteAllTimeNotes()


    // Работа с таблицей PLL_GAME

    suspend fun getAllPllGameItems() = pllGameDao.getAllItems()

    suspend fun insertPllGameItem(pllGameItem: PllGameItem) = pllGameDao.insert(pllGameItem)

    suspend fun insertPllGameItem(pllGameItem: List<PllGameItem>) = pllGameDao.insert(pllGameItem)

    suspend fun deletePllGameItems() = pllGameDao.deleteAllItems()
}