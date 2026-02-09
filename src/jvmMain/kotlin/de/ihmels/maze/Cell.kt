package de.ihmels.maze

import de.ihmels.CellDto
import de.ihmels.Point2D

infix fun Point2D.moveTo(dir: Direction) = Point2D(row + dir.dy, column + dir.dx)

class Cell(row: Int, column: Int) : Point2D(row, column) {

    var northEdge = true
    var eastEdge = true
    var southEdge = true
    var westEdge = true

    fun hasWallInDirection(dir: Direction) = when (dir) {
        Direction.NORTH -> northEdge
        Direction.EAST -> eastEdge
        Direction.SOUTH -> southEdge
        Direction.WEST -> westEdge
    }

    fun connect(cell: Cell) {
        setEdgeByLocation(cell, false)
        cell.setEdgeByLocation(this, false)
    }

    private fun setEdgeByLocation(point: Point2D, value: Boolean) {
        when {
            isBelow(point) -> northEdge = value
            isLeftTo(point) -> eastEdge = value
            isAbove(point) -> southEdge = value
            isRightTo(point) -> westEdge = value
        }
    }

    fun isClosed(): Boolean = northEdge && eastEdge && southEdge && westEdge

    fun toDto(): CellDto = CellDto(row, column, northEdge, eastEdge, southEdge, westEdge)

    fun reset() {
        northEdge = true
        eastEdge = true
        southEdge = true
        westEdge = true
    }

}
