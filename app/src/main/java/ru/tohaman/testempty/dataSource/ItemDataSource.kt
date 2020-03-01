package ru.tohaman.testempty.dataSource

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import ru.tohaman.testempty.dbase.MainDBItem
import ru.tohaman.testempty.dbase.PhaseItem

interface ItemDataSource {

    fun observePhase(phase: String): LiveData<List<MainDBItem>>

    fun getCurrentPhase(): LiveData<PagedList<PhaseItem>>

    suspend fun getPhaseFromMain(phase: String): List<MainDBItem>

    suspend fun getAllItems(): List<MainDBItem>
}