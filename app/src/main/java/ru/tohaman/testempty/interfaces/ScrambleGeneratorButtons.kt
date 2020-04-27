package ru.tohaman.testempty.interfaces

import androidx.lifecycle.MutableLiveData

interface ScrambleGeneratorButtons {
    var isCornerBufferSet: Boolean
    var isEdgeBufferSet: MutableLiveData<Boolean>
    fun generateScramble()
    fun azbukaSelect()
}