package ru.tohaman.rg2.interfaces

import androidx.databinding.ObservableField

interface ScrambleDialogInt {
    var dialogScrambleText: ObservableField<String>
    fun pressMoveButton(letter: String)
    fun pressBackSpace()
    fun pressModifier(modifier: String)
}