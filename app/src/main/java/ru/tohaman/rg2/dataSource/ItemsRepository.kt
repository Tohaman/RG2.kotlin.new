package ru.tohaman.rg2.dataSource

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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
                       private val pllGameDao: PllGameDao,
                       private val oldTimeDao: OldTimeDao,
                       private val oldBaseDao: OldBaseDao) : ItemDataSource {

    // Работа с основной таблицей
    // Кэш для основной базы
    private var allMainDBItems = mutableListOf<MainDBItem>()

    suspend fun getSubMenuList(): List<MainDBItem> {
        if (allMainDBItems.isEmpty()) {
            allMainDBItems = mainDao.getAllItems().toMutableList()     //если кэш пустой, обновляем его
            return mainDao.getSubMenuList()
        } else return allMainDBItems
            .filter { it.url == "submenu" }
            .sortedBy { it.id }
    }

    suspend fun getPhaseFromMain(phase: String): List<MainDBItem>  {
        if (allMainDBItems.isEmpty()) {
            allMainDBItems = mainDao.getAllItems().toMutableList()      //если кэш пустой, обновляем его
            return mainDao.getPhaseFromMain(phase)
        } else return allMainDBItems
            .filter { it.phase == phase }
            .sortedBy { it.id }
    }

    //WHERE phase = :phase and url <> 'submenu' ORDER BY ID
    suspend fun getDetailsItems(phase: String): List<MainDBItem> {
        if (allMainDBItems.isEmpty()) {
            allMainDBItems = mainDao.getAllItems().toMutableList()      //если кэш пустой, обновляем его
            return mainDao.getDetailsItems(phase)
        } else return allMainDBItems
            .filter { (it.phase == phase) and (it.url != "submenu") }
            .sortedBy { it.id }
    }

    suspend fun getItem(phase: String, id: Int): MainDBItem {
        if (allMainDBItems.isEmpty()) {
            allMainDBItems = mainDao.getAllItems().toMutableList()      //если кэш пустой, обновляем его
            return mainDao.getItem(phase, id) ?: MainDBItem("", 0)
        } else return allMainDBItems
            .filter { (it.phase == phase) and (it.id == id) }
            .getOrNull(0) ?: MainDBItem("", 0)
    }

    //WHERE isFavourite = 1 ORDER BY SubID
    suspend fun getFavourites(): List<MainDBItem> {
        if (allMainDBItems.isEmpty()) {
            allMainDBItems = mainDao.getAllItems().toMutableList()      //если кэш пустой, обновляем его
            return mainDao.getFavourites()
        } else return allMainDBItems
            .filter { it.isFavourite }
            .sortedBy { it.subId }
    }

    suspend fun clearMainTable() {
        mainDao.deleteAllItems()
        allMainDBItems.clear()
    }

    suspend fun insert2Main(item: MainDBItem) {
        mainDao.insert(item)
        allMainDBItems.removeAll {(it.id == item.id) and (it.phase == item.phase)}
        allMainDBItems.add(item)
    }

    suspend fun insert2Main(items: List<MainDBItem>) {
        mainDao.insert(items)
        items.map {item ->
            allMainDBItems.removeAll {(it.id == item.id) and (it.phase == item.phase)}
            allMainDBItems.add(item)
        }
    }

    fun updateMainItem(item: MainDBItem?) {
        mainDao.update(item)
        item?.let {
            allMainDBItems.removeAll { (it.id == item.id) and (it.phase == item.phase) }
            allMainDBItems.add(item)
        }
    }

    fun updateMainItem(items: List<MainDBItem>) {
        mainDao.update(items)
        items.map {item ->
            allMainDBItems.removeAll {(it.id == item.id) and (it.phase == item.phase)}
            allMainDBItems.add(item)
        }
    }

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

    suspend fun getPllGameItem(id: Int) = pllGameDao.getPllItem(id)

    suspend fun insertPllGameItem(pllGameItem: PllGameItem) = pllGameDao.insert(pllGameItem)

    suspend fun insertPllGameItem(pllGameItem: List<PllGameItem>) = pllGameDao.insert(pllGameItem)

    suspend fun updatePllGameItem(pllGameItem: List<PllGameItem>) = pllGameDao.update(pllGameItem)

    suspend fun updatePllGameItem(pllGameItem: PllGameItem) = pllGameDao.update(pllGameItem)

    suspend fun deletePllGameItems() = pllGameDao.deleteAllItems()

    // Работа со старой базой SQLite

    suspend fun getAllOldItems() = oldBaseDao.getAllOldItems()

    suspend fun getAllOldTimes() = oldTimeDao.getAllOldTimeItems()
}