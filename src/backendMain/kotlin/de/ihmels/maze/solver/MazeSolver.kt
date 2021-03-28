package de.ihmels.maze.solver

import de.ihmels.Point2D
import de.ihmels.maze.Maze
import kotlinx.coroutines.flow.Flow

interface MazeSolver {

    fun solve(maze: Maze): Flow<Node<Point2D>?>

}