package ru.tohaman.rg2.dataSource

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ru.tohaman.rg2.BuildConfig
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.dbase.daos.*

import ru.tohaman.rg2.dbase.entitys.*
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * The Repository ist a simple Java class that abstracts the data layer from the rest of the app
 * and mediates between different data sources, like a web service and a local cache. It hides the
 * different database operations (like SQLite queries) and provides a clean API to the ViewModel.

    Since Room doesn’t allow database queries on the main thread, then we use suspend fun
 */

class ItemsRepository (context: Context,
                       private val mainDao : MainDao,
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
    private val fileName = "rg2_repolog.log"
    private val file = File(context.filesDir, fileName)

    init {
        runBlocking(Dispatchers.IO) {
            reloadFullCache()               //перечитываем кэш, при создании репозитория
            Timber.d("$TAG .path = ${file.path} ")
        }
    }

    suspend fun getSubMenuList(): List<MainDBItem> {
        val list = allMainDBItems
            .filter { it.url == "submenu" }
            .sortedBy { it.id }
        return if (list.isEmpty()) {
            mainDao.getSubMenuList()
        } else list
    }

    suspend fun getPhaseFromMain(phase: String): List<MainDBItem>  {
        val list = allMainDBItems
            .filter { it.phase == phase }
            .sortedBy { it.id }
        return if (list.isEmpty()) {
            mainDao.getPhaseFromMain(phase)
        } else list
    }

    //WHERE phase = :phase and url <> 'submenu' ORDER BY ID
    suspend fun getDetailsItems(phase: String): List<MainDBItem> {
        val list = allMainDBItems
            .filter { (it.phase == phase) and (it.url != "submenu") }
            .sortedBy { it.id }
        return if (list.isEmpty()) {
            mainDao.getDetailsItems(phase)
        } else list
    }

    suspend fun getItem(phase: String, id: Int): MainDBItem {
        val item = allMainDBItems
            .filter { (it.phase == phase) and (it.id == id) }
            .getOrNull(0)
        return item ?: (mainDao.getItem(phase, id) ?: MainDBItem("", 0))
    }

    //WHERE isFavourite = 1 ORDER BY SubID
    suspend fun getFavourites(): List<MainDBItem> {
        val list = allMainDBItems
            .filter { it.isFavourite }
            .sortedBy { it.subId }
        Timber.d("$TAG .repo getFavourites ${list.size} - $list")
        return if (list.isEmpty())
            mainDao.getFavourites()
        else list
    }

    suspend fun clearMainTable() {
        allMainDBItems.clear()
        mainDao.deleteAllItems()
    }

    suspend fun insertItemToMain(item: MainDBItem) {
        mainDao.insert(item)
        allMainDBItems.removeAll { (it.id == item.id) and (it.phase == item.phase) }
        allMainDBItems.add(item)
    }

    suspend fun insertListToMain(items: List<MainDBItem>) {
        mainDao.insert(items)
        items.map { item ->
            allMainDBItems.removeAll { (it.id == item.id) and (it.phase == item.phase) }
            allMainDBItems.add(item)
        }
    }

    suspend fun updateMainItem(item: MainDBItem?) {
        Timber.d("$TAG .repo updateMainItem - $item")
        if (mainDao.update(item) > 0) {
            item?.let {
                if (allMainDBItems.any { (it.id == item.id) and (it.phase == item.phase) }) {
                    allMainDBItems.removeAll { (it.id == item.id) and (it.phase == item.phase) }
                    allMainDBItems.add(item)
                }
            }
        } else {
            saveDataToLog("$TAG Не смогли обновить в базе: $item")
        }
    }

    suspend fun updateMainItem(items: List<MainDBItem>) {
        Timber.d("$TAG .repo updateMainItemList - ${items.size} - $items")
        if (mainDao.update(items) > 0) {
            items.map { item ->
                if (allMainDBItems.any { (it.id == item.id) and (it.phase == item.phase) }) {
                    allMainDBItems.removeAll { (it.id == item.id) and (it.phase == item.phase) }
                    allMainDBItems.add(item)
                }
            }
        } else {
            saveDataToLog("$TAG Не смогли обновить в базе: $items")
        }
    }

    suspend fun reloadFullCache() {
        Timber.d("$TAG .repo reloadFullCache ")
        allMainDBItems.clear()
        allMainDBItems = mainDao.getAllItems().toMutableList()     //если кэш пустой, обновляем его
        saveDataToLog("$TAG Обновили кэш, получили ${allMainDBItems.size} из базы")
    }

    private fun saveDataToLog(data: String) {
        if (BuildConfig.DEBUG) {
            try {
                val sdf = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
                val currentDate = sdf.format(Date())
                file.writeText("$currentDate $data")
            } catch (e: Exception) {
                Timber.e("$TAG Не смогли записать данные $data в лог. Причина: $e")
            }
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