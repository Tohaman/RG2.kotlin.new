package ru.tohaman.rg2.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.rg2.databinding.FragmentSettingsBinding
import ru.tohaman.rg2.dbase.FillDB

class SettingsFragment  : Fragment() {
    private val settingsViewModel by sharedViewModel<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSettingsBinding.inflate (inflater, container, false)
            .apply {
                viewModel = settingsViewModel
            }


        runBlocking (Dispatchers.IO){
            //Пересоздаем базу при каждом запуске этого фрагмента, чтобы не пересоздавать при каждом входе
            context?.let { FillDB.updateDB(it) }
        }
        return binding.root
    }
}