package de.ihmels.maze

class Cell(row: Int, column: Int) : Location(row, column) {

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

    private fun getEdgeByLocation(location: Location) = when {
        isAbove(location) -> ::southEdge
        isBelow(location) -> ::northEdge
        isLeftTo(location) -> ::eastEdge
        isRightTo(location) -> ::westEdge
        else -> null
    }

}