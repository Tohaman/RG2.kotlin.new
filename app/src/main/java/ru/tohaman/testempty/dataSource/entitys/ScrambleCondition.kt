package ru.tohaman.testempty.dataSource.entitys

data class ScrambleCondition (
    var edgeBuffer: Boolean,
    var cornerBuffer: Boolean,
    var length: Int,
    var azbuka: MutableList<String>
)