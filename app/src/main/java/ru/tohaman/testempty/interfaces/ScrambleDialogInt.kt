package ru.tohaman.testempty.interfaces

import android.view.View
import android.widget.TextView
import androidx.databinding.ObservableField


interface ScrambleDialogInt {
    var dialogScrambleText: ObservableField<String>
    fun pressMoveButton(letter: String)
    fun pressBackSpace()
    fun pressModifier(modifier: String)
}