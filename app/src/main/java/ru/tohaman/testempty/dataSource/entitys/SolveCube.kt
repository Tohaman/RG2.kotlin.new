package ru.tohaman.testempty.dataSource.entitys

data class SolveCube (
    var cube: IntArray,
    var solve: String
)   // куб, решение
{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SolveCube

        if (!cube.contentEquals(other.cube)) return false
        if (solve != other.solve) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cube.contentHashCode()
        result = 31 * result + solve.hashCode()
        return result
    }
}