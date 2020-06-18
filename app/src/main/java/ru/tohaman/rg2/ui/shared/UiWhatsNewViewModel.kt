package ru.tohaman.rg2.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.tohaman.rg2.utils.NonNullMutableLiveData

class UiWhatsNewViewModel: ViewModel() {

    private val _showWhatsNew = NonNullMutableLiveData(true)
    val showWhatsNew: LiveData<Boolean>
        get() = _showWhatsNew

    fun showWhatsNew() {
        _showWhatsNew.value = true
    }

    fun hideWhatsNew() {
        _showWhatsNew.value = false
    }

}