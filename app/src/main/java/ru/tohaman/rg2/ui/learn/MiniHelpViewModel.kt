package ru.tohaman.rg2.ui.learn

import android.content.SharedPreferences
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.rg2.Constants.HELP_COUNT
import ru.tohaman.rg2.Constants.ON_START_MINI_HELP
import ru.tohaman.rg2.Constants.galleryDrawables
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.dataSource.entitys.TipsItem
import ru.tohaman.rg2.utils.NonNullMutableLiveData
import timber.log.Timber

class MiniHelpViewModel: ViewModel(), KoinComponent {
    private val sp = get<SharedPreferences>()

    private var _onStartMiniHelpEnabled = NonNullMutableLiveData(true)
    val onStartMiniHelpEnabled : LiveData<Boolean> get() = _onStartMiniHelpEnabled

    private var _tipsItem = galleryDrawables[0]
    val tipsItem = ObservableField<TipsItem>(_tipsItem)

    private var _helpCount = sp.getInt(HELP_COUNT, 0)

    private fun showingMiniHelpNumber(): Int {
        val maxHelpCount = galleryDrawables.count()
        return _helpCount % maxHelpCount                           //возвращаем остаток от деления
    }

    fun nextHelp() {
        _helpCount += 1
        _tipsItem = galleryDrawables[showingMiniHelpNumber()]
        tipsItem.set(_tipsItem)
    }

    //Проверяем нужно ли отображать миниХелп
    fun checkMiniHelpShow() {

        _onStartMiniHelpEnabled.postValue(sp.getBoolean(ON_START_MINI_HELP, true))
        _tipsItem = galleryDrawables[showingMiniHelpNumber()]
        tipsItem.set(_tipsItem)
    }

    fun closeAndDoNotShowInSession() {
        _helpCount += 1
        Timber.d("$TAG .closeAndDoNotShowInSession $_helpCount")
        sp.edit().putInt(HELP_COUNT, _helpCount).apply()
        _onStartMiniHelpEnabled.postValue(false)
    }

    fun isDoNotShowCheckBoxEnabled(): Boolean {
        return _helpCount > 3
    }


    fun doNotShowChange(value: Boolean) {
        sp.edit().putBoolean(ON_START_MINI_HELP, !value).apply()
    }
}