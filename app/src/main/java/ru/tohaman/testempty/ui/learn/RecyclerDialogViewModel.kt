package ru.tohaman.testempty.ui.learn

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.dbase.entitys.BasicMove
import ru.tohaman.testempty.dbase.entitys.CubeType
import ru.tohaman.testempty.utils.getPhasesToTypesMap
import timber.log.Timber

class RecyclerDialogViewModel(context: Context): ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val ctx = context
    private var type = "BASIC3X3"
    private var phasesToTypes: MutableMap<String, String> = mutableMapOf()
    private var cubeTypes : List<BasicMove> = listOf()
    private var mutableCubeTypes : MutableLiveData<List<BasicMove>> = MutableLiveData()
    val liveDataCubeTypes get() = mutableCubeTypes

    init {
        phasesToTypes = getPhasesToTypesMap(ctx).toMutableMap()
        //setTypeItems("BEGIN3X3")
    }

    fun setTypeItems(phase: String) {
        type = phasesToTypes.getOrElse(phase) {"BASIC3X3"}
        viewModelScope.launch (Dispatchers.IO){
            cubeTypes = repository.getTypeItems(type)
            mutableCubeTypes.postValue(cubeTypes)
        }
    }



}