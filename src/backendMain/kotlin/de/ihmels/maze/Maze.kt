package de.ihmels.maze

import com.google.common.collect.ImmutableList
import de.ihmels.maze.generator.AldousBroderGenerator
import de.ihmels.maze.solver.DepthFirstSolver
import de.ihmels.maze.solver.toList

val topLeft = { _: Maze -> Location(0, 0) }
val topRight = { maze: Maze -> Location(0, maze.columns - 1) }
val bottomLeft = { maze: Maze -> Location(maze.rows - 1, 0) }
val bottomRight = { maze: Maze -> Location(maze.rows - 1, maze.columns - 1) }

class Maze(
    val rows: Int,
    val columns: Int,
    startInitializer: (maze: Maze) -> Location = topLeft,
    destinationInitializer: (maze: Maze) -> Location = bottomRight
) : Iterable<Cell> {

    val grid = List(rows) { r -> List(columns) { c -> Cell(r, c) } }

    val start = startInitializer(this)
    private val destination = destinationInitializer(this)

    val size
        get() = rows * columns

    val cells
        get() = grid.flatten()

    fun successors(location: Location): List<Location> {
        val cell = this[location]

        return Direction.values().asList().stream()
            .filter { !cell.hasWallInDirection(it) }
            .map(location::moveTo)
            .filter(this::contains)
            .collect(ImmutableList.toImmutableList())
    }

    fun isDestination(location: Location) = location == destination

    operator fun get(location: Location): Cell = grid[location.row][location.column]

    operator fun contains(l: Location) = l.row >= 0 && l.column >= 0 && l.row < rows && l.column < columns

    override fun iterator(): Iterator<Cell> {
        return GridIterator(grid)
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append("Maze [$rows, $columns]\n")

        stringBuilder.append("+")
        for (column in 0 until columns) {
            stringBuilder.append("---+")
        }
        stringBuilder.append("\n")

        for (row in grid) {

            var top = "|"
            var bottom = "+"

            for (cell in row) {
                val body = if (cell == start) " S " else if (cell == destination) " O " else "   "
                val eastBoundary = if (cell.eastEdge) "|" else " "

                top += body + eastBoundary

                val southBoundary = if (cell.southEdge) "---" else "   "
                val corner = "+"

                bottom += southBoundary + corner
            }

            stringBuilder.append(top + "\n")
            stringBuilder.append(bottom + "\n")
        }

        return stringBuilder.toString()
    }

    fun printSolution(path: List<Location>): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append("+")
        for (column in 0 until columns) {
            stringBuilder.append("---+")
        }
        stringBuilder.append("\n")

        for (row in grid) {

            var top = "|"
            var bottom = "+"

            for (cell in row) {
                val body = if (cell in path) " * " else "   "
                val eastBoundary = if (cell.eastEdge) "|" else " "

                top += body + eastBoundary

                val southBoundary = if (cell.southEdge) "---" else "   "
                val corner = "+"

                bottom += southBoundary + corner
            }

            stringBuilder.append(top + "\n")
            stringBuilder.append(bottom + "\n")
        }

        return stringBuilder.toString()
    }
}

fun main() {

    val maze = Maze(5, 5, topLeft)
    val generator = AldousBroderGenerator()

    generator.generate(maze)
    println(maze.toString())

    val solver = DepthFirstSolver()
    val solution = solver.solve(maze)

    solution?.let {
        val path = it.toList()
        println(maze.printSolution(path))
    } ?: println("No solution was found")

}