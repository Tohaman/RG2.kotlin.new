package ru.tohaman.testempty.ui.games

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent

class AzbukaTrainerViewModel: ViewModel(), KoinComponent {

    private val _settingTitle = "Тренировка азбуки"
    var settingsTitle = ObservableField<String>(_settingTitle)

    init {}
}