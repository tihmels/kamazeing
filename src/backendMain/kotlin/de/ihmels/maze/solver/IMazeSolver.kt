package de.ihmels.maze.solver

import de.ihmels.Point2D
import de.ihmels.maze.Maze

interface IMazeSolver {

    fun solve(maze: Maze): Node<Point2D>?

}