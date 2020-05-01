package ru.tohaman.testempty.dataSource.entitys

data class ScrambleCondition (
    var solve: String,
    var edgeBuffer: Boolean,
    var cornerBuffer: Boolean
) {
    var solveLength = solve.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size.toString()
}