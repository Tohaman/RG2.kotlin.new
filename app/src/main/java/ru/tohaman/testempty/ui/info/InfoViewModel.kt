package ru.tohaman.testempty.ui.info

import android.app.Application
import android.content.SharedPreferences
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.tohaman.testempty.R
import ru.tohaman.testempty.dataSource.ItemsRepository

class InfoViewModel(app: Application): AndroidViewModel(app), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val sp = get<SharedPreferences>()
    private val ctx = app.baseContext

    val textAbout = ObservableInt(R.string.about)
}