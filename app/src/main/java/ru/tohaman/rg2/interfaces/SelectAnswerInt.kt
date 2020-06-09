package ru.tohaman.rg2.interfaces

interface SelectAnswerInt {
    fun selectAnswer(selectedName: String)
    fun isNeedToShow(lineNumber: Int): Boolean
}