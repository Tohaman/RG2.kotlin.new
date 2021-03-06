package ru.tohaman.rg2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.rg2.AppSettings
import ru.tohaman.rg2.BuildConfig
import ru.tohaman.rg2.Constants.CURRENT_AZBUKA
import ru.tohaman.rg2.Constants.CUSTOM_AZBUKA
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.R
import ru.tohaman.rg2.dataSource.ItemsRepository
import ru.tohaman.rg2.dataSource.entitys.Favorite
import ru.tohaman.rg2.dataSource.resetCube
import ru.tohaman.rg2.dbase.entitys.AzbukaDBItem
import ru.tohaman.rg2.dbase.entitys.TimeNoteItem
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class MigrationsViewModel(app : Application): AndroidViewModel(app), KoinComponent {
    private val repository : ItemsRepository by inject()

    fun migrationToNewVersion() {
        //номер текущей версии программы
        val lastVersion = AppSettings.version
        val currentVersion = BuildConfig.VERSION_CODE
        Timber.d("$TAG .migrationToNewVersion lastVersion = $lastVersion, curVersion = $currentVersion")
        if (lastVersion < 300) updateToVersion300()
        AppSettings.version = currentVersion
    }

    private fun updateToVersion300() {
        migrateStartCount()
        migrateCommentsToRoomBase()
        migrateTimerTimesToRoomBase()
        migrateFavourite()
        migrateAzbuka()
    }

    private fun migrateStartCount() {
        AppSettings.startCount += 1
    }

    private fun migrateCommentsToRoomBase() {
        viewModelScope.launch (Dispatchers.IO) {
            val list = repository.getAllOldItems().filter { it.comment != "" }
            list.map {
                val roomItem = repository.getItem(it.phase, it.id)
                roomItem.comment = it.comment
                repository.updateMainItem(roomItem)
            }
        }
    }

    private fun migrateTimerTimesToRoomBase() {
        viewModelScope.launch (Dispatchers.IO) {
            val list = repository.getAllOldTimes()
            list.map {
                val calendar = convertStringToCalendar(it.dateOfNote)
                val roomTime = TimeNoteItem(0, it.currentTime, calendar, it.scramble, it.timeComment)
                repository.insertTimeNote(roomTime)
            }
        }
    }

    fun migrateFavourite() {
        val setOfFavorite = getFavoriteListFromSharedPref()
        viewModelScope.launch (Dispatchers.IO) {
            val list = repository.getFavourites().toMutableList()
            setOfFavorite.map {
                Timber.d("$TAG .migrateFavourite $it")
                val item = repository.getItem(it.phase, it.id)
                item.favComment = it.comment
                item.isFavourite = true
                item.subId = list.size
                list.add(item)
            }
            repository.updateMainItem(list)
        }
    }

    private fun migrateAzbuka() {
        viewModelScope.launch (Dispatchers.IO) {
            val list = repository.getAllOldItems().filter { it.phase == "AZBUKA" }
            if (list.isNotEmpty()) {
                val currentAzbuka = list.filter { it.id == 0 }[0].comment.split(" ")
                updateDBAzbuka(CURRENT_AZBUKA, currentAzbuka)
                val customAzbuka = list.filter { it.id == 1 }[0].comment.split(" ")
                updateDBAzbuka(CUSTOM_AZBUKA, customAzbuka)
                Timber.d("$TAG .migrateAzbuka complete $currentAzbuka")
            }
        }
    }

    private suspend fun updateDBAzbuka(azbukaName: String, azbuka: List<String>) {
        //Получим из текущего состояния списка отображаемого в GridView (108 элементов)
        //два "чистых" списка с буквами и цветами
        val coloredCube = resetCube()
        val dbAzbuka = mutableListOf<AzbukaDBItem>()
        azbuka.forEachIndexed { index, it ->
            dbAzbuka.add(AzbukaDBItem(azbukaName, index, it, coloredCube[index]))
        }
        repository.updateAzbuka(dbAzbuka)
    }

    //-------------------Различные преобразования-------------------------

    private fun convertStringToCalendar(dateTime: String): Calendar {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        val calendar = Calendar.getInstance()
        try {
            calendar.time = sdf.parse(dateTime)!!
        } catch (exception: Exception) {
            Timber.e(exception)
        }
        return calendar
    }


    private fun getFavoriteListFromSharedPref() : MutableSet<Favorite> {
        val ctx = getApplication<Application>().applicationContext

        var json = AppSettings.favorites
        if (json == "empty") json = ctx.getString(R.string.def_favorites)
        val gson = GsonBuilder().create()
        val itemsListType = object : TypeToken<MutableSet<Favorite>>() {}.type
        return gson.fromJson(json, itemsListType)
    }
}