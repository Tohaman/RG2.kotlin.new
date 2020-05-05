package ru.tohaman.testempty.ui.games

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.entitys.TimeNoteItem
import java.util.*

class TimerResultViewModel: ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()

    private var _timeNotesList = MutableLiveData<List<TimeNoteItem>>()
    val timeNotesList: LiveData<List<TimeNoteItem>> get() = _timeNotesList

    var editedItem = ObservableField<TimeNoteItem>()

    var editedComment = ObservableField<String>()

    fun updateList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.getAllTimeNotes()
            _timeNotesList.postValue(list)
        }
    }

    fun deleteItem(item: TimeNoteItem) {
        viewModelScope.launch {
            repository.deleteTimeNote(item)
        }
        updateList()
    }

    fun updateItem(item: TimeNoteItem) {
        viewModelScope.launch {
            editedComment.set(item.comment)
            repository.updateTimeNote(item)
        }
        updateList()
    }
}