package de.ihmels.maze.solver

import de.ihmels.maze.Maze
import de.ihmels.maze.Point2D
import de.ihmels.maze.graph.TreeNode

interface IMazeSolver {

    fun solve(maze: Maze): TreeNode<Point2D>?

}