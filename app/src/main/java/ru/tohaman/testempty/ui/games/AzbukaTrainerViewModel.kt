package ru.tohaman.testempty.ui.games

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent

class AzbukaTrainerViewModel: ViewModel(), KoinComponent {

    var title = ObservableField("Тренировка азбуки")

}