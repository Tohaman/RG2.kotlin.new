package ru.tohaman.testempty.ui.games

import android.content.SharedPreferences
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import ru.tohaman.testempty.Constants
import ru.tohaman.testempty.dataSource.ItemsRepository
import ru.tohaman.testempty.interfaces.SetLetterButtonsInt

class AzbukaTrainerViewModel: ViewModel(), KoinComponent {
    private val repository : ItemsRepository by inject()
    private val sp = get<SharedPreferences>()

    private val _trainingCorners = sp.getBoolean(Constants.TRAINING_CORNERS, true)
    val trainingCorners = ObservableBoolean(_trainingCorners)

    private val _trainingEgdes = sp.getBoolean(Constants.TRAINING_EDGES, true)
    val trainingEdges = ObservableBoolean(_trainingEgdes)




}