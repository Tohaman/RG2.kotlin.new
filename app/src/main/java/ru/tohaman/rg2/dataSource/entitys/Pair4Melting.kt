package ru.tohaman.rg2.dataSource.entitys

import java.util.*

data class Pair4Melting (
    var allComplete: Boolean,
    var elementsNotOnPlace: SortedMap<Int, Int>
)