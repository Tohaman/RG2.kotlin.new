package ru.tohaman.testempty.ui.games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import ru.tohaman.testempty.dbase.entitys.TimeNoteItem

class TimerResultViewModel: ViewModel(), KoinComponent {

    var _timeNotesList = MutableLiveData<List<TimeNoteItem>>()
    val timeNoteList: LiveData<List<TimeNoteItem>> get() = _timeNotesList

    init {
        val list =
    }
}