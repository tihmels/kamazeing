package de.ihmels.maze

open class Point2D(val row: Int, val column: Int) {

    fun isAbove(point: Point2D) = point.row == row + 1 && point.column == column

    fun isBelow(point: Point2D) = point.row == row - 1 && point.column == column

    fun isLeftTo(point: Point2D) = point.column == column + 1 && point.row == row

    fun isRightTo(point: Point2D) = point.column == column - 1 && point.row == row

    infix fun moveTo(dir: Direction) = Point2D(row + dir.dy, column + dir.dx)

    override fun toString(): String {
        return "Point[$row:$column]"
    }

    override fun equals(other: Any?): Boolean =
        if (other is Point2D) {
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