package de.ihmels.maze.solver

import de.ihmels.maze.Location
import de.ihmels.maze.Maze

interface IMazeSolver {

    fun solve(maze: Maze): Node<Location>?

}