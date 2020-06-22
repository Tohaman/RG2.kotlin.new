package ru.tohaman.rg2.ui.learn

import android.content.SharedPreferences
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import ru.tohaman.rg2.Constants
import ru.tohaman.rg2.Constants.HELP_COUNT
import ru.tohaman.rg2.Constants.ON_START_MINI_HELP
import ru.tohaman.rg2.Constants.PAYED_COINS
import ru.tohaman.rg2.Constants.START_COUNT
import ru.tohaman.rg2.Constants.galleryDrawables
import ru.tohaman.rg2.DebugTag.TAG
import ru.tohaman.rg2.dataSource.entitys.TipsItem
import ru.tohaman.rg2.utils.NonNullMutableLiveData
import ru.tohaman.rg2.utils.SingleLiveEvent
import timber.log.Timber

class MiniHelpViewModel: ViewModel(), KoinComponent {
    private val sp = get<SharedPreferences>()

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

    private var _onStartMiniHelpEnabled = NonNullMutableLiveData(true)
    val onStartMiniHelpEnabled : LiveData<Boolean> get() = _onStartMiniHelpEnabled

    //Проверяем нужно ли отображать миниХелп
    fun checkMiniHelpShow() {
        _onStartMiniHelpEnabled.postValue(sp.getBoolean(ON_START_MINI_HELP, true))
        _tipsItem = galleryDrawables[showingMiniHelpNumber()]
        tipsItem.set(_tipsItem)
    }

    //СинглЛайвЭвент сработает во фрагменте только один раз при вызове .call() во viewModel
    val onStartOpenDonate  = SingleLiveEvent<Nothing>()

    //Проверяем нужно ли перейти на страничку доната
    fun checkDonationShow() {
        val isUserDonate = sp.getInt(PAYED_COINS, 0)
        val startCount = sp.getInt(START_COUNT, 1)
        Timber.d("$TAG .checkDonationShow $startCount $isUserDonate")
        //Если пользователь не платил, то каждый 15 вход переводим на окно Доната
        if ((isUserDonate == 0) and (startCount % 3 == 0)) {
            //Поставим закладку на страничку с донатом
            sp.edit().putInt(Constants.INFO_BOOKMARK, 1).apply()
            onStartOpenDonate.call()
            //Увеличим счетчик входов, чтобы повторно не вызвалось
            sp.edit().putInt(START_COUNT, startCount + 1 ).apply()
        }
    }


    fun closeHelpAndDoNotShowInSession() {
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