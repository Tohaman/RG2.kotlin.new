package ru.tohaman.testempty.dataSource

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import ru.tohaman.testempty.dbase.MainDBItem
import ru.tohaman.testempty.dbase.PhaseItem

interface ItemDataSource {

    fun observePhase(phase: String): LiveData<List<MainDBItem>>

    fun getCurrentPhase(): LiveData<PagedList<PhaseItem>>

    fun getPhaseFromMain(phase: String): LiveData<List<MainDBItem>>

    fun getAllItems(): LiveData<List<MainDBItem>>
}