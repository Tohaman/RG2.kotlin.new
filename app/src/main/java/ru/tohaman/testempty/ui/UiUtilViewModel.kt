package ru.tohaman.testempty.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.tohaman.testempty.utils.NonNullMutableLiveData

class UiUtilViewModel: ViewModel() {

    private val _showBottomNav = NonNullMutableLiveData(true)
    val backPressed = NonNullMutableLiveData(false)
    val bottomNavVisible: LiveData<Boolean>
        get() = _showBottomNav

    fun showBottomNav() {
        _showBottomNav.value = true
    }

    fun hideBottomNav() {
        _showBottomNav.value = false
    }

}