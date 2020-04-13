package ru.tohaman.testempty.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.tohaman.testempty.DebugTag.TAG
import ru.tohaman.testempty.databinding.FragmentSettingsBinding
import ru.tohaman.testempty.dbase.FillDB
import ru.tohaman.testempty.ui.shared.UiUtilViewModel
import timber.log.Timber

class SettingsFragment2  : Fragment() {
    private val uiUtilViewModel by sharedViewModel<UiUtilViewModel>()
    private val settingsViewModel by sharedViewModel<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentSettingsBinding.inflate (inflater, container, false)
            .apply {

                settingsViewModel.setDefaultTheme()
                //darkTheme.isChecked = true

                settingsViewModel.radioChecked.observe(viewLifecycleOwner, Observer {
                    Timber.d("$TAG checked - $it")

                })
                viewModel = settingsViewModel
            }


        runBlocking (Dispatchers.IO){
            //Пересоздаем базу при каждом запуске этого фрагмента, чтобы не пересоздавать при каждом входе
            context?.let { FillDB.reCreateDB(it) }
        }
        return binding.root
    }
}