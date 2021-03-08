package de.ihmels.maze

open class Location(val row: Int, val column: Int) {

    fun isAbove(location: Location) = location.row == row + 1 && location.column == column

    fun isBelow(location: Location) = location.row == row - 1 && location.column == column

    fun isLeftTo(location: Location) = location.column == column + 1 && location.row == row

    fun isRightTo(location: Location) = location.column == column - 1 && location.row == row

    infix fun moveTo(dir: Direction) = Location(row + dir.dy, column + dir.dx)

    override fun equals(other: Any?): Boolean =
        if (other is Location) {
            row == other.row && column == other.column
        } else {
            false
        }

    override fun hashCode(): Int {
        var hash = 23
        hash = hash * 31 + row
        hash = hash * 31 + column
        return hash
    }

}