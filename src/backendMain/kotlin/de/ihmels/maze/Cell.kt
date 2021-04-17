package de.ihmels.maze

import de.ihmels.CellDto
import de.ihmels.Point2D

infix fun Point2D.moveTo(dir: Direction) = Point2D(row + dir.dy, column + dir.dx)

class Cell(row: Int, column: Int) : Point2D(row, column) {

    var northEdge = true
    var eastEdge = true
    var southEdge = true
    var westEdge = true

    fun hasWallInDirection(dir: Direction) = getEdgeByDirection(dir).get()

    fun connect(cell: Cell) {
        getEdgeByLocation(cell)?.set(false)
        cell.getEdgeByLocation(this)?.set(false)
    }

    private fun getEdgeByDirection(dir: Direction) = when (dir) {
        Direction.NORTH -> ::northEdge
        Direction.EAST -> ::eastEdge
        Direction.SOUTH -> ::southEdge
        Direction.WEST -> ::westEdge
    }

    private fun getEdgeByLocation(point: Point2D) = when {
        isBelow(point) -> ::northEdge
        isLeftTo(point) -> ::eastEdge
        isAbove(point) -> ::southEdge
        isRightTo(point) -> ::westEdge
        else -> null
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