package ru.tohaman.testempty.interfaces

import android.widget.TextView

interface SelectAnswerInt {
    fun selectAnswer(selectedName: String)
    fun isNeedToShow(lineNumber: Int): Boolean
}