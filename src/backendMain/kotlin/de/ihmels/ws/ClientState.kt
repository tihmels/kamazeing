package de.ihmels.ws

import de.ihmels.Point2D
import de.ihmels.maze.Maze
import de.ihmels.maze.solver.Node

data class ClientState(
    val maze: Maze = Maze(rows = 10, columns = 10),
    val initialized: Boolean = false,
    val path: Node<Point2D>? = null,
    val generatorDelay: Long = 300L,
    val solverDelay: Long = 300L
)