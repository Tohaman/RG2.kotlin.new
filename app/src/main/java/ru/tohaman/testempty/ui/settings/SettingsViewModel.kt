package ru.tohaman.testempty.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.R
import timber.log.Timber

class SettingsViewModel: ViewModel() {

    var radioChecked = MutableLiveData<Int>()

    init {
        val v1 = R.id.dark_theme
        Timber.d("$TAG defVal - $v1")
        radioChecked.postValue(v1)
    }

    fun setDefaultTheme() {
        val v1 = R.id.dark_theme
        Timber.d("$TAG defVal - $v1")
        radioChecked.postValue(v1)
    }

}