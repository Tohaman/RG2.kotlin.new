package ru.tohaman.rg2.interfaces

import androidx.databinding.ObservableField

interface WrongAnswerInt {
    val wrongAnswerText: ObservableField<String>
    fun stopGame()
    fun continueGame()
}