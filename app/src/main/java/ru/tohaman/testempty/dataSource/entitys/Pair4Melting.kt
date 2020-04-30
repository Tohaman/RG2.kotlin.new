package ru.tohaman.testempty.dataSource.entitys

import java.util.*

data class Pair4Melting (
    var allComplete: Boolean,
    var elementsNotOnPlace: SortedMap<Int, Int>
)