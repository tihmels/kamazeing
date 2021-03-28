package de.ihmels.maze

import com.google.common.collect.ImmutableList
import de.ihmels.MazeDto
import de.ihmels.Point2D
import de.ihmels.maze.generator.BinaryTreeGenerator
import de.ihmels.maze.solver.DepthFirstSolver
import de.ihmels.tree.TreeBuilder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.runBlocking

val topLeft = { _: Maze -> Point2D(0, 0) }
val topRight = { maze: Maze -> Point2D(0, maze.columns - 1) }
val bottomLeft = { maze: Maze -> Point2D(maze.rows - 1, 0) }
val bottomRight = { maze: Maze -> Point2D(maze.rows - 1, maze.columns - 1) }

class Maze(
    val rows: Int = 5,
    val columns: Int = 5,
    startInitializer: (maze: Maze) -> Point2D = topLeft,
    goalInitializer: (maze: Maze) -> Point2D = bottomRight
) {

    var grid = List(rows) { row -> List(columns) { column -> Cell(row, column) } }

    val start: Point2D = startInitializer(this).let { if (contains(it)) it else topLeft(this) }
    val goal = goalInitializer(this)

    val dimension
        get() = Pair(rows, columns)

    val size
        get() = rows * columns

    val cells
        get() = grid.flatten()

    fun successors(point: Point2D): List<Cell> {
        val cell = getCell(point)

        return Direction.values().asList().stream()
            .filter { !cell.hasWallInDirection(it) }
            .map { point.moveTo(it) }
            .filter { this.contains(it) }
            .map { this.getCell(it) }
            .collect(ImmutableList.toImmutableList())
    }

    fun manhattanDistance(point: Point2D): Double {
        val xDist = Math.abs(point.column - goal.column)
        val yDist = Math.abs(point.row - goal.row)
        return (xDist + yDist).toDouble()
    }

    fun isGoal(point: Point2D) = point == goal

    fun getCell(point: Point2D) = grid[point.row][point.column]

    fun toDto() = MazeDto(rows, columns, start, goal, cells.map(Cell::toDto))

    fun reset() {
        for (cell in cells) {
            cell.reset()
        }
    }

    operator fun contains(l: Point2D) = l.row >= 0 && l.column >= 0 && l.row < rows && l.column < columns

    override fun toString(): String = toString(emptyList())
    private fun toString(path: List<Point2D> = emptyList()): String {
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

fun main() = runBlocking {

    val maze = Maze(6, 6)
    val generator = BinaryTreeGenerator()

    println("Generate Maze")

    generator.generate(maze).launchIn(this).join()
    println(maze.toString())

    val solver = DepthFirstSolver()
    val solution = solver.solve(maze)

    val graph = TreeBuilder.createTree(maze.start, maze::successors)


}