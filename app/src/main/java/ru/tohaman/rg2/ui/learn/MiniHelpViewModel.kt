package ru.tohaman.rg2.ui.learn

import android.content.SharedPreferences
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.rg2.Constants.HELP_COUNT
import ru.tohaman.rg2.Constants.NOT_SHOW_MORE
import ru.tohaman.rg2.Constants.galleryDrawables
import ru.tohaman.rg2.dataSource.entitys.TipsItem
import ru.tohaman.rg2.utils.NonNullMutableLiveData

class MiniHelpViewModel: ViewModel(), KoinComponent {
    private val sp = get<SharedPreferences>()

    private var _notShowMore = NonNullMutableLiveData(false)
    val notShowMore : LiveData<Boolean> get() = _notShowMore

    private var _tipsItem = galleryDrawables[showingMiniHelpNumber()]
    val tipsItem = ObservableField<TipsItem>(_tipsItem)

    private var _helpCount = sp.getInt(HELP_COUNT, 0)
    //val startCount = ObservableBoolean(_startCount)

    private fun showingMiniHelpNumber(): Int {
        val maxHelpCount = galleryDrawables.count()
        return _helpCount % maxHelpCount                           //возвращаем остаток от деления
    }

    fun nextHelp() {
        _helpCount += 1
        _tipsItem = galleryDrawables[showingMiniHelpNumber()]
        tipsItem.set(_tipsItem)
        sp.edit().putInt(HELP_COUNT, _helpCount).apply()
    }

    fun checkMiniHelp() {
        _notShowMore.postValue(sp.getBoolean(NOT_SHOW_MORE, false))
    }

}