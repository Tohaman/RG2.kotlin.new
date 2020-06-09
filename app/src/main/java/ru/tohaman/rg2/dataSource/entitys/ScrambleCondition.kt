package ru.tohaman.rg2.dataSource.entitys

data class ScrambleCondition (
    var solve: String,
    var edgeBuffer: Boolean,
    var cornerBuffer: Boolean
) {
    var solveLength = solve.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size.toString()
}